import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
    View,
    Text,
    StyleSheet,
    ActivityIndicator,
    FlatList,
    Alert,
    Animated,
} from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import type { RootStackParamList } from '../navigation/types';
import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PressableScale } from '../components/PressableScale';
import { PlaceCard } from '../components/PlaceCard';
import { FilterChips } from '../components/FilterChips';
import { NumberField } from '../components/NumberField';

import { usePlaces } from '../hooks/usePlaces';
import type { PlacesFilters, PlaceDTO } from '../types/places';
import { getTextStyle, theme } from '../theme/theme';

import { useAuth } from '../context/AuthContext';
import { useTripContext } from '../context/TripContext';
import { useTrip } from '../hooks/useTrip';

type Props = NativeStackScreenProps<RootStackParamList, 'PlacesList'>;

// Keep in sync with backend enum values (case-sensitive in your API)
const CATEGORY_OPTIONS = ['CAFE', 'FOOD', 'HOTEL', 'FUEL', 'OTHER'];

export const PlacesListScreen: React.FC<Props> = ({ navigation, route }) => {
    const { routeId } = route.params;

    const { accessToken } = useAuth();
    const { currentTripId, setCurrentTripId } = useTripContext();

    const { tripQuery, createTripMutation, updateStopsMutation } = useTrip(
        accessToken,
        currentTripId,
    );

    // Draft values (user edits freely)
    const [draftCategory, setDraftCategory] = useState<string | null>(null);
    const [draftMinRating, setDraftMinRating] = useState<string>('');
    const [draftMaxDetour, setDraftMaxDetour] = useState<string>('');

    // Applied filters (query runs only when Apply pressed)
    const [applied, setApplied] = useState<PlacesFilters>({});

    const { data, isLoading, isFetching, error, refetch } = usePlaces(routeId, applied);
    const places = data ?? [];

    const isMutatingTrip = createTripMutation.isPending || updateStopsMutation.isPending;

    const ensureTripId = useCallback(async (): Promise<string> => {
        if (!accessToken) throw new Error('Missing access token');
        if (currentTripId) return currentTripId;

        const created = await createTripMutation.mutateAsync({
            routeId,
            title: 'My Trip',
        });

        setCurrentTripId(created.id);
        return created.id;
    }, [accessToken, currentTripId, createTripMutation, routeId, setCurrentTripId]);

    const onAddToTrip = useCallback(
        async (place: PlaceDTO) => {
            try {
                if (!accessToken) {
                    Alert.alert('Login required', 'Please login to add places to a trip.');
                    return;
                }

                const tripId = await ensureTripId();
                const existingStopsCount = tripQuery.data?.stops?.length ?? 0;

                await updateStopsMutation.mutateAsync({
                    tripId,
                    req: {
                        add: [
                            {
                                placeId: place.id,
                                placeName: place.name?.trim() ? place.name : null,
                                orderIndex: existingStopsCount,
                                plannedArrivalTime: null,
                                plannedDurationMinutes: null,
                            },
                        ],
                        removeIds: [],
                    },
                });

                Alert.alert('Added', 'Place added to your trip.');
            } catch (e: any) {
                Alert.alert('Add to trip failed', e?.message ?? 'Unknown error');
            }
        },
        [accessToken, ensureTripId, tripQuery.data?.stops, updateStopsMutation],
    );

    const goToTripPlanner = useCallback(() => {
        navigation.navigate('TripPlanner', { tripId: currentTripId });
    }, [navigation, currentTripId]);

    const confirmClearTrip = useCallback(() => {
        if (!currentTripId) return;

        Alert.alert('Clear trip?', 'This will remove the active trip from the app context.', [
            { text: 'Cancel', style: 'cancel' },
            { text: 'Clear', style: 'destructive', onPress: () => setCurrentTripId(null) },
        ]);
    }, [currentTripId, setCurrentTripId]);

    const onApply = useCallback(() => {
        const minRating = draftMinRating.trim() === '' ? undefined : Number(draftMinRating);
        const maxDetourKm = draftMaxDetour.trim() === '' ? undefined : Number(draftMaxDetour);

        const next: PlacesFilters = {
            category: draftCategory ?? undefined,
            minRating: Number.isFinite(minRating) ? minRating : undefined,
            maxDetourKm: Number.isFinite(maxDetourKm) ? maxDetourKm : undefined,
        };

        setApplied(next);
    }, [draftCategory, draftMinRating, draftMaxDetour]);

    const onReset = useCallback(() => {
        setDraftCategory(null);
        setDraftMinRating('');
        setDraftMaxDetour('');
        setApplied({});
    }, []);

    // Light list enter animation
    const animated = useRef(new Animated.Value(0)).current;

    useEffect(() => {
        animated.setValue(0);
        Animated.timing(animated, { toValue: 1, duration: 360, useNativeDriver: true }).start();
    }, [animated, applied.category, applied.minRating, applied.maxDetourKm]);

    const renderItem = useCallback(
        ({ item, index }: { item: PlaceDTO; index: number }) => {
            // Keep it subtle: cap the offset to avoid huge gaps with long lists
            const extra = Math.min(index, 12) * 1.5;

            const translateY = animated.interpolate({
                inputRange: [0, 1],
                outputRange: [10 + extra, 0],
            });

            const opacity = animated.interpolate({
                inputRange: [0, 1],
                outputRange: [0, 1],
            });

            return (
                <Animated.View style={{ opacity, transform: [{ translateY }] }}>
                    <PlaceCard place={item} onAddToTrip={onAddToTrip} />
                </Animated.View>
            );
        },
        [animated, onAddToTrip],
    );

    const statusText = useMemo(() => {
        if (isFetching && !isLoading) return 'Updating…';
        return `${places.length} result(s)`;
    }, [isFetching, isLoading, places.length]);

    return (
        <Screen noPadding>
            <AppBar
                title="Places"
                right={{
                    label: 'Trip',
                    onPress: goToTripPlanner,
                    disabled: !currentTripId,
                }}
            />

            <View style={styles.container}>
                <View style={styles.tripStatusBar}>
                    <View>
                        <Text style={styles.tripStatusTitle}>Trip</Text>
                        <Text style={styles.tripStatusText}>
                            {currentTripId ? 'Active' : 'None'} {isMutatingTrip ? '(saving…) ' : ''}
                        </Text>
                    </View>

                    <PressableScale
                        onPress={confirmClearTrip}
                        disabled={!currentTripId || isMutatingTrip}
                        contentStyle={styles.clearTripBtn}
                    >
                        <Text style={styles.clearTripText}>Clear</Text>
                    </PressableScale>
                </View>

                <View style={styles.filtersCard}>
                    <Text style={styles.sectionTitle}>Filters</Text>

                    <FilterChips
                        label="Category"
                        options={CATEGORY_OPTIONS}
                        value={draftCategory}
                        onChange={setDraftCategory}
                        helperText="Select a category or leave empty to see all."
                    />

                    <NumberField
                        label="Minimum rating"
                        value={draftMinRating}
                        onChangeText={setDraftMinRating}
                        placeholder="e.g. 4.2"
                        helperText="0–5. Leave empty to ignore."
                    />

                    <NumberField
                        label="Maximum detour (km)"
                        value={draftMaxDetour}
                        onChangeText={setDraftMaxDetour}
                        placeholder="e.g. 5"
                        helperText="How far off-route you're willing to go."
                    />

                    <View style={styles.actionsRow}>
                        {/* ✅ IMPORTANT: flex must be on wrapper (style), not contentStyle */}
                        <PressableScale onPress={onReset} style={{ flex: 1 }} contentStyle={styles.secondaryBtn}>
                            <Text style={styles.secondaryBtnText}>Reset</Text>
                        </PressableScale>

                        <PressableScale onPress={onApply} style={{ flex: 1 }} contentStyle={styles.primaryBtn}>
                            <Text style={styles.primaryBtnText}>Apply</Text>
                        </PressableScale>
                    </View>

                    <View style={styles.statusRow}>
                        <Text style={styles.statusText}>{statusText}</Text>

                        <PressableScale onPress={() => refetch()} contentStyle={styles.refreshBtn}>
                            <Text style={styles.refreshText}>Refresh</Text>
                        </PressableScale>
                    </View>
                </View>

                {isLoading ? (
                    <View style={styles.center}>
                        <ActivityIndicator size="large" color={theme.colors.primary} />
                        <Text style={styles.loadingText}>Loading places…</Text>
                    </View>
                ) : error ? (
                    <View style={styles.center}>
                        <Text style={styles.errorTitle}>Could not load places</Text>
                        <Text style={styles.errorText}>Please try again. (routeId: {routeId})</Text>

                        <PressableScale onPress={() => refetch()} contentStyle={styles.retryBtn}>
                            <Text style={styles.retryText}>Retry</Text>
                        </PressableScale>
                    </View>
                ) : places.length === 0 ? (
                    <View style={styles.center}>
                        <Text style={styles.emptyTitle}>No places found</Text>
                        <Text style={styles.emptyText}>Try changing your filters.</Text>
                    </View>
                ) : (
                    <FlatList
                        data={places}
                        keyExtractor={(item) => item.id}
                        contentContainerStyle={styles.listContent}
                        renderItem={renderItem}
                        showsVerticalScrollIndicator={false}
                    />
                )}
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        paddingHorizontal: theme.spacing.lg,
        paddingTop: theme.spacing.lg,
    },

    tripStatusBar: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: theme.spacing.md,
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.sm,
        borderRadius: theme.radius.md,
        backgroundColor: 'rgba(2, 6, 23, 0.35)',
        borderWidth: 1,
        borderColor: theme.colors.border,
    },

    tripStatusTitle: {
        color: theme.colors.text,
        ...getTextStyle('bodyMedium'),
    },

    tripStatusText: {
        color: theme.colors.textMuted,
        marginTop: 2,
        ...getTextStyle('caption'),
    },

    clearTripBtn: {
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.xs,
        borderRadius: theme.radius.pill,
        backgroundColor: theme.colors.dangerSoft,
        borderWidth: 1,
        borderColor: 'rgba(249, 115, 115, 0.35)',
    },

    clearTripText: {
        color: theme.colors.danger,
        ...getTextStyle('overline'),
    },

    filtersCard: {
        borderRadius: theme.radius.lg,
        padding: theme.spacing.md,
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderWidth: 1,
        borderColor: theme.colors.border,
        marginBottom: theme.spacing.md,
    },

    sectionTitle: {
        color: theme.colors.text,
        ...getTextStyle('h2'),
        marginBottom: theme.spacing.xs,
    },

    actionsRow: {
        flexDirection: 'row',
        gap: theme.spacing.sm,
        marginTop: theme.spacing.md,
    },

    secondaryBtn: {
        paddingVertical: theme.spacing.sm,
        borderRadius: theme.radius.md,
        alignItems: 'center',
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: theme.colors.border,
    },

    secondaryBtnText: {
        color: theme.colors.textMuted,
        ...getTextStyle('bodyMedium'),
    },

    primaryBtn: {
        paddingVertical: theme.spacing.sm,
        borderRadius: theme.radius.md,
        alignItems: 'center',
        backgroundColor: theme.colors.primarySoft,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },

    primaryBtnText: {
        color: theme.colors.primary,
        ...getTextStyle('bodyMedium'),
    },

    statusRow: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginTop: theme.spacing.md,
    },

    statusText: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },

    refreshBtn: {
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.xs,
        borderRadius: theme.radius.pill,
        backgroundColor: theme.colors.primarySoft,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },

    refreshText: {
        color: theme.colors.primary,
        ...getTextStyle('overline'),
    },

    center: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: theme.spacing.lg,
    },

    loadingText: {
        color: theme.colors.textMuted,
        marginTop: theme.spacing.sm,
        ...getTextStyle('body'),
    },

    errorTitle: {
        color: theme.colors.text,
        ...getTextStyle('h2'),
        textAlign: 'center',
        marginBottom: theme.spacing.xs,
    },

    errorText: {
        color: theme.colors.danger,
        ...getTextStyle('body'),
        textAlign: 'center',
    },

    retryBtn: {
        marginTop: theme.spacing.md,
        paddingHorizontal: theme.spacing.lg,
        paddingVertical: theme.spacing.sm,
        borderRadius: theme.radius.pill,
        backgroundColor: theme.colors.dangerSoft,
        borderWidth: 1,
        borderColor: 'rgba(249, 115, 115, 0.35)',
    },

    retryText: {
        color: theme.colors.danger,
        ...getTextStyle('bodyMedium'),
    },

    emptyTitle: {
        color: theme.colors.text,
        ...getTextStyle('h2'),
        textAlign: 'center',
        marginBottom: theme.spacing.xs,
    },

    emptyText: {
        color: theme.colors.textMuted,
        ...getTextStyle('body'),
        textAlign: 'center',
    },

    listContent: {
        paddingBottom: theme.spacing.xl,
    },
});

export default PlacesListScreen;
