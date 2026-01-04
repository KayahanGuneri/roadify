// src/screens/TripPlannerScreen.tsx

import React, { useMemo } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList, ActivityIndicator } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import type { RootStackParamList } from '../navigation/RootStack';
import { Screen } from '../components/Screen';
import { colors, spacing } from '../theme/theme';
import { useTripContext } from '../context/TripContext';
import { useTrip } from '../hooks/useTrip';
import { useAuth } from '../context/AuthContext';

type Props = NativeStackScreenProps<RootStackParamList, 'TripPlanner'>;

export const TripPlannerScreen: React.FC<Props> = ({ navigation, route }) => {
    const { accessToken } = useAuth();
    const { currentTripId, setCurrentTripId } = useTripContext();

    // route.params optional → safe read
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

    return (
        <Screen>
            <View style={styles.container}>
                <View style={styles.header}>
                    <TouchableOpacity onPress={() => navigation.goBack()}>
                        <Text style={styles.backText}>‹ Back</Text>
                    </TouchableOpacity>

                    <Text style={styles.title}>Trip</Text>

                    <TouchableOpacity
                        onPress={() => {
                            setCurrentTripId(null);
                            navigation.goBack();
                        }}
                    >
                        <Text style={styles.clearText}>Clear</Text>
                    </TouchableOpacity>
                </View>

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
                        <ActivityIndicator size="large" color={colors.primary} />
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
                                    <View style={{ flex: 1 }}>
                                        <Text style={styles.stopTitle}>Place: {item.placeId}</Text>
                                        <Text style={styles.stopSub}>Order: {item.orderIndex}</Text>
                                    </View>

                                    <TouchableOpacity
                                        onPress={() => onRemove(item.id)}
                                        disabled={updateStopsMutation.isPending}
                                        style={styles.removeBtn}
                                    >
                                        <Text style={styles.removeText}>Remove</Text>
                                    </TouchableOpacity>
                                </View>
                            )}
                        />
                    </>
                )}
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, paddingHorizontal: spacing.lg, paddingTop: spacing.lg },
    header: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: spacing.md },
    backText: { color: colors.textPrimary, fontSize: 16 },
    title: { color: colors.textPrimary, fontSize: 20, fontWeight: '800' },
    clearText: { color: colors.primary, fontSize: 14, fontWeight: '800' },
    center: { flex: 1, alignItems: 'center', justifyContent: 'center', paddingHorizontal: spacing.lg },
    loadingText: { color: colors.textPrimary, marginTop: spacing.sm },
    errorTitle: { color: colors.textPrimary, fontSize: 16, fontWeight: '800', marginBottom: spacing.xs, textAlign: 'center' },
    errorText: { color: '#F97373', textAlign: 'center' },
    emptyTitle: { color: colors.textPrimary, fontSize: 16, fontWeight: '800', textAlign: 'center', marginBottom: spacing.xs },
    emptyText: { color: colors.textSecondary, textAlign: 'center' },
    tripCard: { borderRadius: 16, padding: spacing.md, backgroundColor: 'rgba(2, 6, 23, 0.55)', borderWidth: 1, borderColor: 'rgba(255,255,255,0.08)', marginBottom: spacing.md },
    tripTitle: { color: colors.textPrimary, fontSize: 16, fontWeight: '900' },
    tripMeta: { color: colors.textSecondary, marginTop: spacing.xs },
    listContent: { paddingBottom: spacing.xl },
    stopRow: { flexDirection: 'row', alignItems: 'center', padding: spacing.md, borderRadius: 16, backgroundColor: 'rgba(255,255,255,0.06)', borderWidth: 1, borderColor: 'rgba(255,255,255,0.10)', marginBottom: spacing.md },
    stopTitle: { color: colors.textPrimary, fontWeight: '800' },
    stopSub: { color: colors.textSecondary, marginTop: 4, fontSize: 12 },
    removeBtn: { paddingHorizontal: spacing.md, paddingVertical: spacing.xs, borderRadius: 999, backgroundColor: 'rgba(249, 115, 115, 0.15)', borderWidth: 1, borderColor: 'rgba(249, 115, 115, 0.35)' },
    removeText: { color: '#F97373', fontWeight: '800', fontSize: 12 },
});

export default TripPlannerScreen;
