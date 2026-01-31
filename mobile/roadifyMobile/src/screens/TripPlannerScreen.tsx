import React, { useMemo, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, Alert } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import type { RootStackParamList } from '../navigation/types';
import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PressableScale } from '../components/PressableScale';
import { getTextStyle, theme } from '../theme/theme';
import { useTripContext } from '../context/TripContext';
import { useTrip } from '../hooks/useTrip';
import { useAuth } from '../context/AuthContext';

type Props = NativeStackScreenProps<RootStackParamList, 'TripPlanner'>;

export const TripPlannerScreen: React.FC<Props> = ({ navigation, route }) => {
    const { accessToken } = useAuth();
    const { currentTripId, setCurrentTripId } = useTripContext();

    const tripIdFromParams = route.params?.tripId ?? null;
    const tripId = tripIdFromParams ?? currentTripId;

    const { tripQuery, updateStopsMutation } = useTrip(accessToken, tripId);

    const stops = useMemo(() => {
        const list = tripQuery.data?.stops ?? [];
        return [...list].sort((a, b) => a.orderIndex - b.orderIndex);
    }, [tripQuery.data?.stops]);

    const onRemove = useCallback(
        async (stopId: string) => {
            if (!tripId) return;
            try {
                await updateStopsMutation.mutateAsync({
                    tripId,
                    req: { add: [], removeIds: [stopId] },
                });
            } catch {
                // UI phase: keep it simple
            }
        },
        [tripId, updateStopsMutation],
    );

    const onClear = useCallback(() => {
        if (!tripId) return;

        Alert.alert('Clear trip?', 'This will remove the active trip from the app context.', [
            { text: 'Cancel', style: 'cancel' },
            {
                text: 'Clear',
                style: 'destructive',
                onPress: () => {
                    setCurrentTripId(null);
                    navigation.goBack();
                },
            },
        ]);
    }, [navigation, setCurrentTripId, tripId]);

    const headerRightDisabled = !tripId || updateStopsMutation.isPending;

    return (
        <Screen background="living" noPadding>
            <AppBar title="Trip" right={{ label: 'Clear', onPress: onClear, disabled: headerRightDisabled }} />

            <View style={styles.container}>
                {!accessToken ? (
                    <View style={styles.center}>
                        <Text style={styles.errorTitle}>Missing access token</Text>
                        <Text style={styles.errorText}>Please login again.</Text>
                    </View>
                ) : !tripId ? (
                    <View style={styles.center}>
                        <Text style={styles.emptyTitle}>No trip selected</Text>
                        <Text style={styles.emptyText}>Create a trip from Places first, then come back here.</Text>
                    </View>
                ) : tripQuery.isLoading ? (
                    <View style={styles.center}>
                        <ActivityIndicator size="large" color={theme.colors.primary} />
                        <Text style={styles.loadingText}>Loading trip…</Text>
                    </View>
                ) : tripQuery.error ? (
                    <View style={styles.center}>
                        <Text style={styles.errorTitle}>Could not load trip</Text>
                        <Text style={styles.errorText}>Please try again.</Text>
                    </View>
                ) : !tripQuery.data ? (
                    <View style={styles.center}>
                        <Text style={styles.emptyTitle}>Trip not found</Text>
                        <Text style={styles.emptyText}>tripId: {tripId}</Text>
                    </View>
                ) : (
                    <>
                        <View style={styles.tripCard}>
                            <Text style={styles.tripTitle}>{tripQuery.data.title}</Text>
                            <Text style={styles.tripMeta}>
                                {stops.length} stop(s) • {tripQuery.data.routeId ? 'Route linked' : 'No route'}
                            </Text>
                        </View>

                        <FlatList
                            data={stops}
                            keyExtractor={(s) => s.id}
                            contentContainerStyle={styles.listContent}
                            renderItem={({ item, index }) => {
                                const placeLabel = item.placeName?.trim() ? item.placeName : item.placeId;
                                return (
                                    <View style={styles.stopRow}>
                                        <View style={styles.badge}>
                                            <Text style={styles.badgeText}>{index + 1}</Text>
                                        </View>

                                        <View style={styles.stopLeft}>
                                            <Text style={styles.stopTitle} numberOfLines={1}>
                                                {placeLabel}
                                            </Text>
                                            <Text style={styles.stopSub}>Stop #{index + 1}</Text>
                                        </View>

                                        <PressableScale
                                            onPress={() => onRemove(item.id)}
                                            disabled={updateStopsMutation.isPending}
                                            contentStyle={styles.removeBtn}
                                        >
                                            <Text style={styles.removeText}>
                                                {updateStopsMutation.isPending ? 'Removing…' : 'Remove'}
                                            </Text>
                                        </PressableScale>
                                    </View>
                                );
                            }}
                            showsVerticalScrollIndicator={false}
                        />

                        {stops.length === 0 ? (
                            <View style={styles.centerInline}>
                                <Text style={styles.emptyText}>No stops yet. Add places from the Places screen.</Text>
                            </View>
                        ) : null}
                    </>
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

    center: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: theme.spacing.lg,
    },

    centerInline: {
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: theme.spacing.lg,
        paddingVertical: theme.spacing.md,
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

    tripCard: {
        borderRadius: theme.radius.lg,
        padding: theme.spacing.md,
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderWidth: 1,
        borderColor: theme.colors.border,
        marginBottom: theme.spacing.md,
    },

    tripTitle: {
        color: theme.colors.text,
        ...getTextStyle('h2'),
    },

    tripMeta: {
        color: theme.colors.textMuted,
        marginTop: theme.spacing.xs,
        ...getTextStyle('caption'),
    },

    listContent: { paddingBottom: theme.spacing.xl },

    stopRow: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: theme.spacing.md,
        borderRadius: theme.radius.lg,
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: theme.colors.border,
        marginBottom: theme.spacing.md,
        gap: theme.spacing.md,
    },

    badge: {
        width: 28,
        height: 28,
        borderRadius: theme.radius.pill,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: theme.colors.primarySoft,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },

    badgeText: {
        color: theme.colors.primary,
        ...getTextStyle('overline'),
    },

    stopLeft: { flex: 1 },

    stopTitle: {
        color: theme.colors.text,
        ...getTextStyle('bodyMedium'),
    },

    stopSub: {
        color: theme.colors.textMuted,
        marginTop: 4,
        ...getTextStyle('caption'),
    },

    removeBtn: {
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.xs,
        borderRadius: theme.radius.pill,
        backgroundColor: theme.colors.dangerSoft,
        borderWidth: 1,
        borderColor: 'rgba(249, 115, 115, 0.35)',
    },

    removeText: {
        color: theme.colors.danger,
        ...getTextStyle('overline'),
    },
});

export default TripPlannerScreen;
