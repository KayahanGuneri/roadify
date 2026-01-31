import React, { useEffect, useMemo, useState } from 'react';
import { View, Text, StyleSheet, ActivityIndicator } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PrimaryButton } from '../components/PrimaryButton';
import type { RootStackParamList } from '../navigation/types';
import { useRouteById } from '../hooks/useRouteById';
import { RouteMap } from '../components/RouteMap';
import { RouteMapPlaceholder } from '../components/RouteMapPlaceholder';
import { getElevation, getTextStyle, theme } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'RoutePreview'>;
type MapStatus = 'unknown' | 'ready' | 'fallback';

export const RoutePreviewScreen: React.FC<Props> = ({ navigation, route }) => {
    // Params (safe)
    const routeId = route.params?.routeId ?? '';
    const fromCity = route.params?.fromCity ?? '';
    const toCity = route.params?.toCity ?? '';

    // Hooks (always same order)
    const { data, isLoading, error, refetch, isFetching } = useRouteById(routeId);
    const [mapStatus, setMapStatus] = useState<MapStatus>('unknown');

    useEffect(() => {
        // Reset when route changes
        setMapStatus('unknown');

        // If map isn't ready quickly, show fallback placeholder
        const t = setTimeout(() => {
            setMapStatus((s) => (s === 'ready' ? 'ready' : 'fallback'));
        }, 900);

        return () => clearTimeout(t);
    }, [routeId]);

    const onMapReady = () => setMapStatus('ready');

    const distanceKm = useMemo(() => data?.distanceKm ?? 0, [data]);
    const durationMinutes = useMemo(() => data?.durationMinutes ?? 0, [data]);
    const hours = Math.floor(durationMinutes / 60);
    const minutes = Math.round(durationMinutes % 60);

    // Missing routeId (after hooks)
    if (!routeId) {
        return (
            <Screen>
                <AppBar title="Route Preview" onBack={() => navigation.navigate('Home')} />
                <View style={styles.center}>
                    <Text style={styles.title}>Route Preview</Text>
                    <Text style={styles.errorText}>Missing route id. Please select a route again.</Text>
                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: theme.spacing.md }}
                    />
                </View>
            </Screen>
        );
    }

    // Loading
    if (isLoading) {
        return (
            <Screen style={styles.screenNoPadding}>
                <View style={styles.container}>
                    {/* ✅ No header in placeholder (AppBar already exists) */}
                    <RouteMapPlaceholder fromCity={fromCity} toCity={toCity} showHeader={false} />
                    <AppBar title="Route Preview" variant="overlay" />

                    <View style={[styles.bottomPanel, getElevation('e3')]}>
                        <View style={styles.centerInline}>
                            <ActivityIndicator size="small" color={theme.colors.primary} />
                            <Text style={styles.loadingText}>Loading route…</Text>
                        </View>
                    </View>
                </View>
            </Screen>
        );
    }

    // Error / No data
    if (error || !data) {
        return (
            <Screen style={styles.screenNoPadding}>
                <View style={styles.container}>
                    <RouteMapPlaceholder fromCity={fromCity} toCity={toCity} showHeader={false} />
                    <AppBar title="Route Preview" variant="overlay" />

                    <View style={[styles.bottomPanel, getElevation('e3')]}>
                        <Text style={styles.errorTitle}>Could not load route</Text>
                        <Text style={styles.errorText}>Please try again.</Text>

                        <PrimaryButton
                            title={isFetching ? 'Retrying…' : 'Retry'}
                            onPress={() => refetch()}
                            disabled={isFetching}
                            style={{ marginTop: theme.spacing.md }}
                        />

                        <PrimaryButton
                            title="Back to Home"
                            onPress={() => navigation.navigate('Home')}
                            style={{ marginTop: theme.spacing.sm }}
                        />
                    </View>
                </View>
            </Screen>
        );
    }

    return (
        <Screen style={styles.screenNoPadding}>
            <View style={styles.container}>
                {/* ✅ Key fix: Try map first. Only show full fallback after timeout. */}
                {mapStatus === 'fallback' ? (
                    <RouteMapPlaceholder fromCity={fromCity} toCity={toCity} showHeader={false} />
                ) : (
                    <View style={{ flex: 1 }}>
                        <RouteMap route={data} onReady={onMapReady} />

                        {/* While unknown, overlay a lightweight placeholder */}
                        {mapStatus === 'unknown' ? (
                            <View style={StyleSheet.absoluteFill} pointerEvents="none">
                                <RouteMapPlaceholder fromCity={fromCity} toCity={toCity} showHeader={false} />
                            </View>
                        ) : null}
                    </View>
                )}

                <AppBar title="Route Preview" variant="overlay" />

                <View style={[styles.bottomPanel, getElevation('e3')]}>
                    <View style={styles.locationsRow}>
                        <View style={styles.locationBlock}>
                            <Text style={styles.smallLabel}>From</Text>
                            <Text style={styles.smallValue} numberOfLines={1}>
                                {fromCity}
                            </Text>
                        </View>

                        <View style={styles.locationBlock}>
                            <Text style={styles.smallLabel}>To</Text>
                            <Text style={styles.smallValue} numberOfLines={1}>
                                {toCity}
                            </Text>
                        </View>
                    </View>

                    <View style={styles.divider} />

                    <View style={styles.statsRow}>
                        <View style={styles.stat}>
                            <Text style={styles.statLabel}>Distance</Text>
                            <Text style={styles.statValue}>{distanceKm.toFixed(1)} km</Text>
                        </View>

                        <View style={styles.stat}>
                            <Text style={styles.statLabel}>Duration</Text>
                            <Text style={styles.statValue}>
                                {hours} h {minutes} m
                            </Text>
                        </View>
                    </View>

                    <PrimaryButton
                        title="Discover places on this route"
                        onPress={() => navigation.navigate('PlacesList', { routeId })}
                        style={{ marginTop: theme.spacing.md }}
                    />

                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: theme.spacing.sm }}
                    />

                    {mapStatus !== 'ready' ? (
                        <Text style={styles.hintText}>Map is unavailable right now. Showing a preview placeholder.</Text>
                    ) : null}
                </View>
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1 },

    screenNoPadding: {
        paddingHorizontal: 0,
        paddingTop: 0,
        paddingBottom: 0,
    },

    center: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        padding: theme.spacing.lg,
    },

    centerInline: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        gap: theme.spacing.sm,
    },

    loadingText: {
        color: theme.colors.textMuted,
        ...getTextStyle('body'),
    },

    title: {
        color: theme.colors.text,
        ...getTextStyle('h1'),
    },

    errorTitle: {
        color: theme.colors.text,
        ...getTextStyle('h2'),
    },

    errorText: {
        color: theme.colors.danger,
        marginTop: theme.spacing.xs,
        textAlign: 'left',
        ...getTextStyle('body'),
    },

    bottomPanel: {
        position: 'absolute',
        left: theme.spacing.lg,
        right: theme.spacing.lg,
        bottom: theme.spacing.lg,
        padding: theme.spacing.md,
        borderRadius: theme.radius['2xl'],
        backgroundColor: 'rgba(2, 6, 23, 0.92)',
        borderWidth: 1,
        borderColor: theme.colors.border,
    },

    divider: {
        height: 1,
        backgroundColor: theme.colors.border,
        marginTop: theme.spacing.sm,
    },

    locationsRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        gap: theme.spacing.md,
    },

    locationBlock: { flex: 1 },

    smallLabel: {
        color: theme.colors.textMuted,
        ...getTextStyle('overline'),
    },

    smallValue: {
        color: theme.colors.text,
        marginTop: theme.spacing.xs,
        ...getTextStyle('bodyMedium'),
    },

    statsRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        gap: theme.spacing.md,
        marginTop: theme.spacing.sm,
    },

    stat: { flex: 1 },

    statLabel: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },

    statValue: {
        color: theme.colors.text,
        marginTop: theme.spacing.xs,
        ...getTextStyle('h2'),
    },

    hintText: {
        marginTop: theme.spacing.sm,
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },
});

export default RoutePreviewScreen;
