import React from 'react';
import { View, Text, StyleSheet, ActivityIndicator } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PrimaryButton } from '../components/PrimaryButton';
import type { RootStackParamList } from '../navigation/types';
import { useRouteById } from '../hooks/useRouteById';
import { RouteMap } from '../components/RouteMap';
import { getElevation, getTextStyle, theme } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'RoutePreview'>;

export const RoutePreviewScreen: React.FC<Props> = ({ navigation, route }) => {
    const { routeId, fromCity, toCity } = route.params;
    const { data, isLoading, error } = useRouteById(routeId);

    if (isLoading) {
        return (
            <Screen>
                <View style={styles.center}>
                    <ActivityIndicator size="large" color={theme.colors.primary} />
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
                    <Text style={styles.errorText}>Could not load route details. Please try again.</Text>

                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: theme.spacing.md }}
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
        <Screen style={styles.screenNoPadding}>
            <View style={styles.container}>
                <RouteMap route={data} />

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

    loadingText: {
        color: theme.colors.textMuted,
        marginTop: theme.spacing.sm,
        ...getTextStyle('body'),
    },

    title: {
        color: theme.colors.text,
        ...getTextStyle('h1'),
    },

    errorText: {
        color: theme.colors.danger,
        marginTop: theme.spacing.xs,
        textAlign: 'center',
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
});

export default RoutePreviewScreen;
