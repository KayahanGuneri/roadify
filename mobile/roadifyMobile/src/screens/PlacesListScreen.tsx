import React, { useMemo, useState } from 'react';
import {
    View,
    Text,
    StyleSheet,
    ActivityIndicator,
    FlatList,
    TextInput,
    Alert,
} from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import type { RootStackParamList } from '../navigation/types';
import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PressableScale } from '../components/PressableScale';
import { PlaceCard } from '../components/PlaceCard';
import { usePlaces } from '../hooks/usePlaces';
import type { PlacesFilters, PlaceDTO } from '../types/places';
import { getTextStyle, theme } from '../theme/theme';

import { useAuth } from '../context/AuthContext';
import { useTripContext } from '../context/TripContext';
import { useTrip } from '../hooks/useTrip';

type Props = NativeStackScreenProps<RootStackParamList, 'PlacesList'>;

export const PlacesListScreen: React.FC<Props> = ({ navigation, route }) => {
    const { routeId } = route.params;

    const { accessToken } = useAuth();
    const { currentTripId, setCurrentTripId } = useTripContext();

    const { tripQuery, createTripMutation, updateStopsMutation } = useTrip(accessToken, currentTripId);

    const [category, setCategory] = useState<string>('');
    const [minRatingText, setMinRatingText] = useState<string>('');
    const [maxDetourKmText, setMaxDetourKmText] = useState<string>('');

    const filters: PlacesFilters = useMemo(() => {
        const minRating = minRatingText.trim() === '' ? undefined : Number(minRatingText);
        const maxDetourKm = maxDetourKmText.trim() === '' ? undefined : Number(maxDetourKmText);

        return {
            category: category.trim() === '' ? undefined : category.trim().toUpperCase(),
            minRating: Number.isFinite(minRating) ? minRating : undefined,
            maxDetourKm: Number.isFinite(maxDetourKm) ? maxDetourKm : undefined,
        };
    }, [category, minRatingText, maxDetourKmText]);

    const { data, isLoading, isFetching, error, refetch } = usePlaces(routeId, filters);
    const places = data ?? [];

    const isMutatingTrip = createTripMutation.isPending || updateStopsMutation.isPending;

    const ensureTripId = async (): Promise<string> => {
        if (!accessToken) throw new Error('Missing access token');
        if (currentTripId) return currentTripId;

        const created = await createTripMutation.mutateAsync({
            routeId,
            title: 'My Trip',
        });

        setCurrentTripId(created.id);
        return created.id;
    };

    const onAddToTrip = async (place: PlaceDTO) => {
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
                            placeName: place.name ?? null,
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
    };

    const goToTripPlanner = () => {
        navigation.navigate('TripPlanner', { tripId: currentTripId });
    };

    const clearTrip = () => setCurrentTripId(null);

    return (
        <Screen>
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
                    <Text style={styles.tripStatusText}>
                        Trip: {currentTripId ? 'ACTIVE' : 'none'} {isMutatingTrip ? '(saving...)' : ''}
                    </Text>

                    <PressableScale
                        onPress={clearTrip}
                        disabled={!currentTripId || isMutatingTrip}
                        contentStyle={styles.clearTripBtn}
                    >
                        <Text style={styles.clearTripText}>Clear</Text>
                    </PressableScale>
                </View>

                <View style={styles.filtersCard}>
                    <Text style={styles.sectionTitle}>Filters</Text>

                    <View style={styles.filterRow}>
                        <Text style={styles.label}>Category</Text>
                        <TextInput
                            value={category}
                            onChangeText={setCategory}
                            placeholder="e.g. FOOD, HOTEL, OTHER"
                            placeholderTextColor="rgba(255,255,255,0.35)"
                            style={styles.input}
                            autoCapitalize="characters"
                        />
                    </View>

                    <View style={styles.filterRow}>
                        <Text style={styles.label}>Min rating</Text>
                        <TextInput
                            value={minRatingText}
                            onChangeText={setMinRatingText}
                            placeholder="e.g. 4"
                            placeholderTextColor="rgba(255,255,255,0.35)"
                            keyboardType="numeric"
                            style={styles.input}
                        />
                    </View>

                    <View style={styles.filterRow}>
                        <Text style={styles.label}>Max detour (km)</Text>
                        <TextInput
                            value={maxDetourKmText}
                            onChangeText={setMaxDetourKmText}
                            placeholder="e.g. 5"
                            placeholderTextColor="rgba(255,255,255,0.35)"
                            keyboardType="numeric"
                            style={styles.input}
                        />
                    </View>

                    <View style={styles.statusRow}>
                        {isFetching && !isLoading ? (
                            <Text style={styles.statusText}>Updating…</Text>
                        ) : (
                            <Text style={styles.statusText}>{places.length} result(s)</Text>
                        )}

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
                        renderItem={({ item }) => <PlaceCard place={item} onAddToTrip={onAddToTrip} />}
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

    tripStatusText: {
        color: theme.colors.textMuted,
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
        marginBottom: theme.spacing.sm,
    },

    filterRow: { marginTop: theme.spacing.sm },

    label: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
        marginBottom: theme.spacing.xs,
    },

    input: {
        height: 42,
        borderRadius: theme.radius.md,
        paddingHorizontal: theme.spacing.md,
        color: theme.colors.text,
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: theme.colors.border,
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
