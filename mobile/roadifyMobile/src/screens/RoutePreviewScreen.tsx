// src/screens/RoutePreviewScreen.tsx
import React from 'react';
import {
    View,
    Text,
    StyleSheet,
    ActivityIndicator,
    TouchableOpacity,
} from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { RootStackParamList } from '../navigation/RootStack';
import { useRouteById } from '../hooks/useRouteById';
import { RouteMap } from '../components/RouteMap';

type Props = NativeStackScreenProps<RootStackParamList, 'RoutePreview'>;

export const RoutePreviewScreen: React.FC<Props> = ({ navigation, route }) => {
    const { routeId, fromCity, toCity } = route.params;

    const { data, isLoading, error } = useRouteById(routeId);

    if (isLoading) {
        return (
            <Screen>
                <ActivityIndicator size="large" color="#6EE7B7" />
                <Text style={styles.loadingText}>Loading route…</Text>
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
            <View style={styles.container}>
                {/* Full-screen map */}
                <RouteMap route={data} />

                {/* Top title overlay (optional) */}
                <View style={styles.topBar}>
                    <TouchableOpacity onPress={() => navigation.goBack()}>
                        <Text style={styles.backText}>‹ Back</Text>
                    </TouchableOpacity>
                    <Text style={styles.topTitle}>Route Preview</Text>
                    <View style={{ width: 40 }} />
                </View>

                {/* Bottom overlay card with stats */}
                <View style={styles.bottomPanel}>
                    <View style={styles.locationsRow}>
                        <View style={styles.locationBlock}>
                            <Text style={styles.smallLabel}>From</Text>
                            <Text style={styles.smallValue}>{fromCity}</Text>
                        </View>
                        <View style={styles.locationBlock}>
                            <Text style={styles.smallLabel}>To</Text>
                            <Text style={styles.smallValue}>{toCity}</Text>
                        </View>
                    </View>

                    <View style={styles.statsRow}>
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

                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: 12 }}
                    />
                </View>
            </View>
        </Screen>
    );
};

const DARK_BG = '#020617';
const PANEL_BG = 'rgba(2, 6, 23, 0.94)';
const PRIMARY = '#34D399';
const TEXT_PRIMARY = '#FFFFFF';
const TEXT_SECONDARY = '#9CA3AF';

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    loadingText: {
        color: '#E5E7EB',
        marginTop: 8,
    },
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
    topBar: {
        position: 'absolute',
        top: 16,
        left: 16,
        right: 16,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    backText: {
        color: TEXT_PRIMARY,
        fontSize: 16,
    },
    topTitle: {
        color: TEXT_PRIMARY,
        fontSize: 18,
        fontWeight: '700',
    },
    bottomPanel: {
        position: 'absolute',
        left: 16,
        right: 16,
        bottom: 24,
        padding: 16,
        borderRadius: 20,
        backgroundColor: PANEL_BG,
    },
    locationsRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: 8,
    },
    locationBlock: {
        flex: 1,
    },
    smallLabel: {
        color: TEXT_SECONDARY,
        fontSize: 11,
    },
    smallValue: {
        color: '#E5E7EB',
        fontSize: 13,
        marginTop: 2,
    },
    statsRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 8,
    },
    stat: {
        flex: 1,
    },
    statLabel: {
        color: TEXT_SECONDARY,
        fontSize: 12,
    },
    statValue: {
        color: TEXT_PRIMARY,
        fontSize: 16,
        fontWeight: '600',
        marginTop: 2,
    },
});

export default RoutePreviewScreen;
