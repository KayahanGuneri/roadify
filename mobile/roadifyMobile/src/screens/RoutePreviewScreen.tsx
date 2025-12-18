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
import type { RootStackParamList } from '../navigation/RootStack';
import { useRouteById } from '../hooks/useRouteById';
import { RouteMap } from '../components/RouteMap';
import { colors, spacing } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'RoutePreview'>;

/**
 * RoutePreviewScreen
 *
 * English:
 * Displays a full-screen map preview for a computed route and basic statistics.
 * Provides navigation to the next step: exploring places along the route (Mobile Phase M3).
 *
 * Türkçe Özet:
 * Hesaplanan rotanın harita önizlemesini ve temel istatistiklerini gösterir.
 * M3 kapsamında "Places" listesine routeId ile geçiş sağlar.
 */
export const RoutePreviewScreen: React.FC<Props> = ({ navigation, route }) => {
    const { routeId, fromCity, toCity } = route.params;

    const { data, isLoading, error } = useRouteById(routeId);

    if (isLoading) {
        return (
            <Screen>
                <View style={styles.center}>
                    <ActivityIndicator size="large" color={colors.primary} />
                    <Text style={styles.loadingText}>Loading route…</Text>
                </View>
            </Screen>
        );
    }

    if (error || !data) {
        return (
            <Screen>
                <View style={styles.center}>
                    <Text style={styles.title}>Route Preview</Text>
                    <Text style={styles.errorText}>
                        Could not load route details. Please try again.
                    </Text>

                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: spacing.md }}
                    />
                </View>
            </Screen>
        );
    }

    const distanceKm = data.distanceKm;
    const durationMinutes = data.durationMinutes;

    const hours = Math.floor(durationMinutes / 60);
    const minutes = Math.round(durationMinutes % 60);

    return (
        <Screen>
            <View style={styles.container}>
                {/* Full-screen map */}
                <RouteMap route={data} />

                {/* Top overlay bar */}
                <View style={styles.topBar}>
                    <TouchableOpacity
                        onPress={() => navigation.goBack()}
                        accessibilityRole="button"
                        accessibilityLabel="Go back"
                    >
                        <Text style={styles.backText}>‹ Back</Text>
                    </TouchableOpacity>

                    <Text style={styles.topTitle}>Route Preview</Text>
                    <View style={{ width: 48 }} />
                </View>

                {/* Bottom overlay panel */}
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
                            <Text style={styles.statValue}>
                                {distanceKm.toFixed(1)} km
                            </Text>
                        </View>

                        <View style={styles.stat}>
                            <Text style={styles.statLabel}>Duration</Text>
                            <Text style={styles.statValue}>
                                {hours} h {minutes} m
                            </Text>
                        </View>
                    </View>

                    {/* M3 CTA */}
                    <PrimaryButton
                        title="Discover places on this route"
                        onPress={() =>
                            navigation.navigate('PlacesList', { routeId })
                        }
                        style={{ marginTop: spacing.md }}
                    />

                    {/* Secondary action (optional, keep for convenience) */}
                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: spacing.sm }}
                    />
                </View>
            </View>
        </Screen>
    );
};

const PANEL_BG = 'rgba(2, 6, 23, 0.94)';

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },

    center: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        padding: spacing.lg,
    },

    loadingText: {
        color: colors.textPrimary,
        marginTop: spacing.sm,
    },

    title: {
        color: colors.textPrimary,
        fontSize: 20,
        fontWeight: '700',
        marginBottom: spacing.sm,
    },

    errorText: {
        color: '#F97373',
        marginTop: spacing.xs,
        textAlign: 'center',
    },

    topBar: {
        position: 'absolute',
        top: spacing.lg,
        left: spacing.lg,
        right: spacing.lg,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
    },

    backText: {
        color: colors.textPrimary,
        fontSize: 16,
    },

    topTitle: {
        color: colors.textPrimary,
        fontSize: 18,
        fontWeight: '700',
    },

    bottomPanel: {
        position: 'absolute',
        left: spacing.lg,
        right: spacing.lg,
        bottom: spacing.lg,
        padding: spacing.md,
        borderRadius: 20,
        backgroundColor: PANEL_BG,
    },

    locationsRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: spacing.sm,
    },

    locationBlock: {
        flex: 1,
    },

    smallLabel: {
        color: colors.textSecondary,
        fontSize: 11,
    },

    smallValue: {
        color: colors.textPrimary,
        fontSize: 13,
        marginTop: spacing.xs,
    },

    statsRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: spacing.sm,
    },

    stat: {
        flex: 1,
    },

    statLabel: {
        color: colors.textSecondary,
        fontSize: 12,
    },

    statValue: {
        color: colors.textPrimary,
        fontSize: 16,
        fontWeight: '600',
        marginTop: spacing.xs,
    },
});

export default RoutePreviewScreen;
