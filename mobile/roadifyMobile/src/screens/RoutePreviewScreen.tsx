// src/screens/RoutePreviewScreen.tsx
import React from 'react';
import { View, Text, StyleSheet, Image, ActivityIndicator } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { RootStackParamList } from '../navigation/RootStack';
import { useRouteById } from '../hooks/useRouteById';

type Props = NativeStackScreenProps<RootStackParamList, 'RoutePreview'>;

export const RoutePreviewScreen: React.FC<Props> = ({ navigation, route }) => {
    const { routeId, fromCity, toCity } = route.params;

    const { data, isLoading, error } = useRouteById(routeId);

    if (isLoading) {
        return (
            <Screen>
                <ActivityIndicator size="large" color="#6EE7B7" />
                <Text style={{ color: '#E5E7EB', marginTop: 8 }}>Loading route…</Text>
            </Screen>
        );
    }

    if (error || !data) {
        return (
            <Screen>
                <Text style={styles.title}>Route Preview</Text>
                <Text style={styles.errorText}>
                    Could not load route details. Please try again.
                </Text>
                <PrimaryButton
                    title="Back to Home"
                    onPress={() => navigation.navigate('Home')}
                    style={{ marginTop: 16 }}
                />
            </Screen>
        );
    }

    const distanceKm = data.distanceKm;
    const durationMinutes = data.durationMinutes;

    return (
        <Screen>
            <Text style={styles.title}>Route Preview</Text>

            {/* ROUTE STATS CARD */}
            <View style={styles.card}>
                <Text style={styles.cardLabel}>Route ID</Text>
                <Text style={styles.cardRouteId}>{routeId}</Text>

                <View style={styles.row}>
                    <View style={styles.stat}>
                        <Text style={styles.statLabel}>Distance</Text>
                        <Text style={styles.statValue}>{distanceKm.toFixed(1)} km</Text>
                    </View>
                    <View style={styles.stat}>
                        <Text style={styles.statLabel}>Duration</Text>
                        <Text style={styles.statValue}>
                            {Math.floor(durationMinutes / 60)} h{' '}
                            {Math.round(durationMinutes % 60)} m
                        </Text>
                    </View>
                </View>

                <View style={styles.locations}>
                    <View style={styles.locationBlock}>
                        <Text style={styles.smallLabel}>From</Text>
                        {/* ✅ Artık şehir adı gösteriyoruz, koordinat değil */}
                        <Text style={styles.smallValue}>{fromCity}</Text>
                    </View>
                    <View style={styles.locationBlock}>
                        <Text style={styles.smallLabel}>To</Text>
                        {/* ✅ Burada da toCity */}
                        <Text style={styles.smallValue}>{toCity}</Text>
                    </View>
                </View>
            </View>

            {/* MINI MAP CARD */}
            <View style={styles.mapCard}>
                <Text style={styles.mapTitle}>Route map</Text>
                <Text style={styles.mapSubtitle}>
                    A visual route preview and animated progress.
                </Text>

                <View style={styles.mapPreviewRow}>
                    <Image
                        source={require('../assets/illustrations/route-preview.png')}
                        style={styles.mapImage}
                        resizeMode="cover"
                    />
                    <View style={styles.mapSideTextWrapper}>
                        <Text style={styles.mapSideText}>
                            Live map coming in{'\n'}Mobile Phase 3.
                        </Text>
                    </View>
                </View>

                <PrimaryButton
                    title="Open Full Map (Soon)"
                    onPress={() =>
                        navigation.navigate('RouteMapFull', {
                            routeId: routeId,
                        })
                    }
                    style={{ marginTop: 12 }}
                />
            </View>

            <PrimaryButton
                title="Back to Home"
                onPress={() => navigation.navigate('Home')}
                style={{ marginTop: 18 }}
            />
        </Screen>
    );
};

const styles = StyleSheet.create({
    title: {
        color: '#FFFFFF',
        fontSize: 20,
        fontWeight: '700',
        marginBottom: 12,
    },
    errorText: {
        color: '#F97373',
        marginTop: 8,
    },
    card: {
        backgroundColor: '#020617',
        borderRadius: 22,
        padding: 18,
        marginBottom: 14,
    },
    cardLabel: {
        color: '#9CA3AF',
        fontSize: 11,
    },
    cardRouteId: {
        color: '#E5E7EB',
        fontWeight: '600',
        marginBottom: 8,
        marginTop: 2,
    },
    row: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 8,
    },
    stat: { flex: 1 },
    statLabel: {
        color: '#9CA3AF',
        fontSize: 12,
    },
    statValue: {
        color: '#FFFFFF',
        fontSize: 16,
        fontWeight: '600',
        marginTop: 2,
    },
    locations: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 12,
    },
    locationBlock: { flex: 1 },
    smallLabel: {
        color: '#9CA3AF',
        fontSize: 11,
    },
    smallValue: {
        color: '#E5E7EB',
        fontSize: 13,
        marginTop: 2,
    },
    mapCard: {
        backgroundColor: '#020617',
        borderRadius: 22,
        padding: 18,
    },
    mapTitle: {
        color: '#FFFFFF',
        fontWeight: '600',
        fontSize: 16,
    },
    mapSubtitle: {
        color: '#9CA3AF',
        marginTop: 4,
        marginBottom: 10,
    },
    mapPreviewRow: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    mapImage: {
        flex: 2,
        height: 90,
        borderRadius: 18,
        marginRight: 8,
    },
    mapSideTextWrapper: {
        flex: 1,
        justifyContent: 'center',
    },
    mapSideText: {
        color: '#E5E7EB',
        fontSize: 11,
        textAlign: 'right',
    },
});

export default RoutePreviewScreen;
