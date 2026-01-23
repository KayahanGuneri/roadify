import React, { useMemo } from 'react';
import { View, Text, StyleSheet, Image, ActivityIndicator, ScrollView } from 'react-native';
import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { useAnalytics } from '../hooks/useAnalytics';

const hero = require('../assets/illustrations/hero.png');

function formatNumber(value: number | undefined): string {
    if (value === null || value === undefined || Number.isNaN(value)) return '0';
    return String(value);
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
        // Eğer backend sıralı değilse, burada date ile max seçebilirsin.
        return aiUsage[aiUsage.length - 1];
    }, [aiUsage]);

    const totalRoutes = overview?.totalRoutes ?? 0;
    const totalAiRequests = overview?.totalAiRequests ?? 0;
    const totalAiAccepted = overview?.totalAiAccepted ?? 0;

    return (
        <Screen>
            <ScrollView contentContainerStyle={styles.container}>
                <View style={styles.card}>
                    <Text style={styles.title}>Analytics</Text>

                    {isLoading ? (
                        <View style={styles.stateBox}>
                            <ActivityIndicator />
                            <Text style={styles.stateText}>Loading analytics...</Text>
                        </View>
                    ) : isError ? (
                        <View style={styles.stateBox}>
                            <Text style={styles.stateText}>We couldn't load analytics right now.</Text>
                            <Text style={styles.stateSubText}>
                                {error ? String(error) : 'Please try again.'}
                            </Text>

                            <PrimaryButton title="Retry" onPress={refetchAll} style={{ marginTop: 12 }} />
                        </View>
                    ) : (
                        <>
                            <Text style={styles.subtitle}>
                                Insights for {range.from} → {range.to}
                            </Text>

                            <Image source={hero} style={styles.image} resizeMode="cover" />

                            <View style={styles.row}>
                                <View style={styles.statBox}>
                                    <Text style={styles.statLabel}>Routes</Text>
                                    <Text style={styles.statValue}>{formatNumber(totalRoutes)}</Text>
                                </View>

                                <View style={styles.statBox}>
                                    <Text style={styles.statLabel}>AI requests</Text>
                                    <Text style={styles.statValue}>{formatNumber(totalAiRequests)}</Text>
                                </View>

                                <View style={styles.statBox}>
                                    <Text style={styles.statLabel}>AI accepted</Text>
                                    <Text style={styles.statValue}>{formatNumber(totalAiAccepted)}</Text>
                                </View>
                            </View>

                            <View style={{ marginTop: 16, gap: 10 }}>
                                <View style={styles.insightBox}>
                                    <Text style={styles.insightTitle}>Popular category</Text>
                                    <Text style={styles.insightValue}>
                                        {topCategory
                                            ? `${topCategory.category} (${topCategory.count}) · ${topCategory.date}`
                                            : 'No data yet'}
                                    </Text>
                                </View>

                                <View style={styles.insightBox}>
                                    <Text style={styles.insightTitle}>AI usage (latest day)</Text>
                                    <Text style={styles.insightValue}>
                                        {lastAiUsage
                                            ? `Date: ${lastAiUsage.date} · Requested: ${formatNumber(lastAiUsage.requestCount)} · Accepted: ${formatNumber(lastAiUsage.acceptedCount)} · Rate: ${lastAiUsage.acceptanceRate}`
                                            : 'No data yet'}
                                    </Text>
                                </View>
                            </View>

                            <PrimaryButton
                                title="Back to Home"
                                onPress={() => navigation.navigate('Home')}
                                style={{ marginTop: 16 }}
                            />
                        </>
                    )}
                </View>
            </ScrollView>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: { padding: 16 },
    card: {
        flex: 1,
        backgroundColor: 'rgba(15,23,42,0.96)',
        borderRadius: 24,
        padding: 20,
    },
    title: {
        color: '#FFFFFF',
        fontSize: 22,
        fontWeight: '700',
        marginBottom: 4,
    },
    subtitle: {
        color: '#9CA3AF',
        fontSize: 14,
        marginBottom: 16,
    },
    image: {
        width: '100%',
        height: 170,
        borderRadius: 20,
        marginBottom: 16,
    },
    row: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        gap: 8,
    },
    statBox: {
        flex: 1,
        backgroundColor: '#020617',
        borderRadius: 16,
        paddingVertical: 12,
        paddingHorizontal: 10,
        alignItems: 'center',
    },
    statLabel: { color: '#9CA3AF', fontSize: 12 },
    statValue: {
        color: '#FFFFFF',
        fontSize: 18,
        fontWeight: '700',
        marginTop: 4,
    },
    stateBox: {
        marginTop: 16,
        backgroundColor: '#020617',
        borderRadius: 16,
        padding: 16,
        gap: 8,
    },
    stateText: { color: '#FFFFFF', fontSize: 14, fontWeight: '600' },
    stateSubText: { color: '#9CA3AF', fontSize: 12 },
    insightBox: {
        backgroundColor: '#020617',
        borderRadius: 16,
        padding: 14,
    },
    insightTitle: { color: '#9CA3AF', fontSize: 12 },
    insightValue: {
        color: '#FFFFFF',
        fontSize: 14,
        fontWeight: '600',
        marginTop: 6,
    },
});
