// src/screens/AnalyticsScreen.tsx
import React, { useMemo } from 'react';
import { View, Text, StyleSheet, Image, ActivityIndicator, ScrollView } from 'react-native';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PrimaryButton } from '../components/PrimaryButton';
import { useAnalytics } from '../hooks/useAnalytics';
import { getElevation, getTextStyle, theme } from '../theme/theme';

const hero = require('../assets/illustrations/hero.png');

function formatNumber(value: number | undefined): string {
    if (value === null || value === undefined || Number.isNaN(value)) return '0';
    return String(value);
}

function formatRate(value: number | undefined): string {
    if (value === null || value === undefined || Number.isNaN(value)) return '—';
    // backend seems to return 0..1 (example: 1.0)
    const pct = Math.round(value * 100);
    return `${pct}%`;
}

export const AnalyticsScreen: React.FC<any> = ({ navigation }) => {
    const { range, overview, popularCategories, aiUsage, isLoading, isError, error, refetchAll } =
        useAnalytics();

    const topCategory = useMemo(() => {
        if (!popularCategories || popularCategories.length === 0) return null;
        return popularCategories[0];
    }, [popularCategories]);

    const lastAiUsage = useMemo(() => {
        if (!aiUsage || aiUsage.length === 0) return null;
        return aiUsage[aiUsage.length - 1];
    }, [aiUsage]);

    const totalRoutes = overview?.totalRoutes ?? 0;
    const totalAiRequests = overview?.totalAiRequests ?? 0;
    const totalAiAccepted = overview?.totalAiAccepted ?? 0;

    return (
        <Screen background="living">
            <AppBar title="Analytics" />

            <ScrollView contentContainerStyle={styles.container} showsVerticalScrollIndicator={false}>
                <View style={styles.card}>
                    {isLoading ? (
                        <View style={styles.stateBox}>
                            <ActivityIndicator color={theme.colors.primary} />
                            <Text style={styles.stateText}>Loading analytics…</Text>
                            <Text style={styles.stateSubText}>Fetching insights for the last 30 days.</Text>
                        </View>
                    ) : isError ? (
                        <View style={styles.stateBox}>
                            <Text style={styles.stateText}>We couldn't load analytics right now.</Text>
                            <Text style={styles.stateSubText}>
                                {error ? String(error) : 'Please try again.'}
                            </Text>

                            <PrimaryButton
                                title="Retry"
                                onPress={refetchAll}
                                style={{ marginTop: theme.spacing.sm }}
                            />
                        </View>
                    ) : (
                        <>
                            <Text style={styles.subtitle}>
                                Insights for {range.from} → {range.to}
                            </Text>

                            <Image source={hero} style={styles.image} resizeMode="cover" />

                            <View style={styles.row}>
                                <View style={styles.statBox}>
                                    <Text style={styles.statIcon}>🧭</Text>
                                    <Text style={styles.statLabel}>Routes</Text>
                                    <Text style={styles.statValue}>{formatNumber(totalRoutes)}</Text>
                                    <Text style={styles.statHint}>Planned routes created</Text>
                                </View>

                                <View style={styles.statBox}>
                                    <Text style={styles.statIcon}>🤖</Text>
                                    <Text style={styles.statLabel}>AI requests</Text>
                                    <Text style={styles.statValue}>{formatNumber(totalAiRequests)}</Text>
                                    <Text style={styles.statHint}>Total AI messages sent</Text>
                                </View>

                                <View style={styles.statBox}>
                                    <Text style={styles.statIcon}>✅</Text>
                                    <Text style={styles.statLabel}>Accepted</Text>
                                    <Text style={styles.statValue}>{formatNumber(totalAiAccepted)}</Text>
                                    <Text style={styles.statHint}>Suggestions added to trips</Text>
                                </View>
                            </View>

                            <View style={styles.insights}>
                                <View style={styles.insightBox}>
                                    <Text style={styles.insightTitle}>Popular category</Text>
                                    <Text style={styles.insightValue}>
                                        {topCategory
                                            ? `${topCategory.category} · ${topCategory.count} place(s)`
                                            : 'No data yet'}
                                    </Text>
                                    <Text style={styles.insightMeta}>
                                        {topCategory ? `Last seen: ${topCategory.date}` : ' '}
                                    </Text>
                                </View>

                                <View style={styles.insightBox}>
                                    <Text style={styles.insightTitle}>AI usage (latest day)</Text>
                                    <Text style={styles.insightValue}>
                                        {lastAiUsage
                                            ? `${formatNumber(lastAiUsage.requestCount)} requested · ${formatNumber(
                                                lastAiUsage.acceptedCount
                                            )} accepted`
                                            : 'No data yet'}
                                    </Text>
                                    <Text style={styles.insightMeta}>
                                        {lastAiUsage
                                            ? `Date: ${lastAiUsage.date} · Acceptance: ${formatRate(
                                                lastAiUsage.acceptanceRate
                                            )}`
                                            : ' '}
                                    </Text>
                                </View>
                            </View>

                            <PrimaryButton
                                title="Back to Home"
                                onPress={() => navigation.navigate('Home')}
                                style={{ marginTop: theme.spacing.md }}
                            />
                        </>
                    )}
                </View>
            </ScrollView>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: {
        padding: theme.spacing.lg,
    },

    card: {
        flex: 1,
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderRadius: theme.radius['2xl'],
        padding: theme.spacing.lg,
        borderWidth: 1,
        borderColor: theme.colors.border,
        ...getElevation('e2'),
    },

    subtitle: {
        color: theme.colors.textMuted,
        ...getTextStyle('body'),
        marginBottom: theme.spacing.md,
    },

    image: {
        width: '100%',
        height: 170,
        borderRadius: theme.radius.xl,
        marginBottom: theme.spacing.md,
        borderWidth: 1,
        borderColor: theme.colors.border,
    },

    row: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        gap: theme.spacing.sm,
    },

    statBox: {
        flex: 1,
        backgroundColor: 'rgba(255,255,255,0.04)',
        borderRadius: theme.radius.lg,
        paddingVertical: theme.spacing.md,
        paddingHorizontal: theme.spacing.sm,
        alignItems: 'center',
        borderWidth: 1,
        borderColor: theme.colors.border,
        ...getElevation('e1'),
    },

    statIcon: {
        fontSize: 18,
        marginBottom: 6,
    },

    statLabel: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },

    statValue: {
        color: theme.colors.text,
        ...getTextStyle('h2'),
        marginTop: theme.spacing.xs,
    },

    statHint: {
        color: theme.colors.textMuted,
        ...getTextStyle('overline'),
        marginTop: 6,
        textAlign: 'center',
    },

    stateBox: {
        marginTop: theme.spacing.md,
        backgroundColor: 'rgba(255,255,255,0.04)',
        borderRadius: theme.radius.lg,
        padding: theme.spacing.md,
        borderWidth: 1,
        borderColor: theme.colors.border,
        gap: theme.spacing.sm,
        ...getElevation('e1'),
    },

    stateText: {
        color: theme.colors.text,
        ...getTextStyle('bodyMedium'),
    },

    stateSubText: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },

    insights: {
        marginTop: theme.spacing.md,
        gap: theme.spacing.sm,
    },

    insightBox: {
        backgroundColor: 'rgba(255,255,255,0.04)',
        borderRadius: theme.radius.lg,
        padding: theme.spacing.md,
        borderWidth: 1,
        borderColor: theme.colors.border,
        ...getElevation('e1'),
    },

    insightTitle: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },

    insightValue: {
        color: theme.colors.text,
        ...getTextStyle('bodyMedium'),
        marginTop: 6,
    },

    insightMeta: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
        marginTop: 6,
    },
});

export default AnalyticsScreen;
