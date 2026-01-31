import React, { useMemo } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator } from 'react-native';
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

    const onRemove = async (stopId: string) => {
        if (!tripId) return;
        await updateStopsMutation.mutateAsync({
            tripId,
            req: { add: [], removeIds: [stopId] },
        });
    };

    const onClear = () => {
        setCurrentTripId(null);
        navigation.goBack();
    };

    return (
        <Screen background="living">
            <AppBar title="Trip" right={{ label: 'Clear', onPress: onClear, disabled: !tripId }} />

            <View style={styles.container}>
                {!accessToken ? (
                    <View style={styles.center}>
                        <Text style={styles.errorTitle}>Missing access token</Text>
                        <Text style={styles.errorText}>Please login again.</Text>
                    </View>
                ) : !tripId ? (
                    <View style={styles.center}>
                        <Text style={styles.emptyTitle}>No trip selected</Text>
                        <Text style={styles.emptyText}>
                            Henüz tripId yok. Önce bir trip oluşturup buraya tripId ile gelmeliyiz.
                        </Text>
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
                            <Text style={styles.tripMeta}>{stops.length} stop(s)</Text>
                        </View>

                        <FlatList
                            data={stops}
                            keyExtractor={(s) => s.id}
                            contentContainerStyle={styles.listContent}
                            renderItem={({ item }) => (
                                <View style={styles.stopRow}>
                                    <View style={styles.stopLeft}>
                                        <Text style={styles.stopTitle} numberOfLines={1}>
                                            Place: {item.placeName ?? item.placeId}
                                        </Text>
                                        <Text style={styles.stopSub}>Order: {item.orderIndex}</Text>
                                    </View>

                                    <PressableScale
                                        onPress={() => onRemove(item.id)}
                                        disabled={updateStopsMutation.isPending}
                                        contentStyle={styles.removeBtn}
                                    >
                                        <Text style={styles.removeText}>Remove</Text>
                                    </PressableScale>
                                </View>
                            )}
                            showsVerticalScrollIndicator={false}
                        />
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
