import React, { useMemo, useState } from 'react';
import {
    View,
    Text,
    StyleSheet,
    ActivityIndicator,
    FlatList,
    TextInput,
    TouchableOpacity,
} from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import type { RootStackParamList } from '../navigation/RootStack';
import { Screen } from '../components/Screen';
import { PlaceCard } from '../components/PlaceCard';
import { usePlaces } from '../hooks/usePlaces';
import type { PlacesFilters } from '../types/places';
import { colors, spacing } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'PlacesList'>;

/**
 * PlacesListScreen
 *
 * English:
 * Fetches and displays route-based places for a given routeId.
 * Provides local filtering controls and refetches data when filters change.
 *
 * Türkçe Özet:
 * routeId için mekanları çeker ve listeler.
 * Filtre state'ini local tutar; filtre değişince React Query otomatik refetch yapar.
 */
export const PlacesListScreen: React.FC<Props> = ({ navigation, route }) => {
    const { routeId } = route.params;

    // Filter state (kept simple for MVP)
    const [category, setCategory] = useState<string>('');
    const [minRatingText, setMinRatingText] = useState<string>('');
    const [maxDetourKmText, setMaxDetourKmText] = useState<string>('');

    const filters: PlacesFilters = useMemo(() => {
        const minRating = minRatingText.trim() === '' ? undefined : Number(minRatingText);
        const maxDetourKm = maxDetourKmText.trim() === '' ? undefined : Number(maxDetourKmText);

        return {
            category: category.trim() === '' ? undefined : category.trim().toUpperCase(),
            minRating: Number.isFinite(minRating) ? minRating : undefined,
            maxDetourKm: Number.isFinite(maxDetourKm) ? maxDetourKm : undefined,
        };
    }, [category, minRatingText, maxDetourKmText]);

    const { data, isLoading, isFetching, error, refetch } = usePlaces(routeId, filters);

    const places = data ?? [];

    return (
        <Screen>
            <View style={styles.container}>
                {/* Header */}
                <View style={styles.header}>
                    <TouchableOpacity
                        onPress={() => navigation.goBack()}
                        accessibilityRole="button"
                        accessibilityLabel="Go back"
                    >
                        <Text style={styles.backText}>‹ Back</Text>
                    </TouchableOpacity>

                    <Text style={styles.title}>Places</Text>
                    <View style={{ width: 48 }} />
                </View>

                {/* Filters */}
                <View style={styles.filtersCard}>
                    <Text style={styles.sectionTitle}>Filters</Text>

                    <View style={styles.filterRow}>
                        <Text style={styles.label}>Category</Text>
                        <TextInput
                            value={category}
                            onChangeText={setCategory}
                            placeholder="e.g. FOOD, HOTEL, OTHER"
                            placeholderTextColor="rgba(255,255,255,0.35)"
                            style={styles.input}
                            autoCapitalize="characters"
                        />
                    </View>

                    <View style={styles.filterRow}>
                        <Text style={styles.label}>Min rating</Text>
                        <TextInput
                            value={minRatingText}
                            onChangeText={setMinRatingText}
                            placeholder="e.g. 4"
                            placeholderTextColor="rgba(255,255,255,0.35)"
                            keyboardType="numeric"
                            style={styles.input}
                        />
                    </View>

                    <View style={styles.filterRow}>
                        <Text style={styles.label}>Max detour (km)</Text>
                        <TextInput
                            value={maxDetourKmText}
                            onChangeText={setMaxDetourKmText}
                            placeholder="e.g. 5"
                            placeholderTextColor="rgba(255,255,255,0.35)"
                            keyboardType="numeric"
                            style={styles.input}
                        />
                    </View>

                    {/* Small status line */}
                    <View style={styles.statusRow}>
                        {isFetching && !isLoading ? (
                            <Text style={styles.statusText}>Updating…</Text>
                        ) : (
                            <Text style={styles.statusText}>
                                {places.length} result(s)
                            </Text>
                        )}

                        <TouchableOpacity onPress={() => refetch()} style={styles.refreshBtn}>
                            <Text style={styles.refreshText}>Refresh</Text>
                        </TouchableOpacity>
                    </View>
                </View>

                {/* Content */}
                {isLoading ? (
                    <View style={styles.center}>
                        <ActivityIndicator size="large" color={colors.primary} />
                        <Text style={styles.loadingText}>Loading places…</Text>
                    </View>
                ) : error ? (
                    <View style={styles.center}>
                        <Text style={styles.errorTitle}>Could not load places</Text>
                        <Text style={styles.errorText}>
                            Please try again. (routeId: {routeId})
                        </Text>

                        <TouchableOpacity onPress={() => refetch()} style={styles.retryBtn}>
                            <Text style={styles.retryText}>Retry</Text>
                        </TouchableOpacity>
                    </View>
                ) : places.length === 0 ? (
                    <View style={styles.center}>
                        <Text style={styles.emptyTitle}>No places found</Text>
                        <Text style={styles.emptyText}>
                            Try changing your filters.
                        </Text>
                    </View>
                ) : (
                    <FlatList
                        data={places}
                        keyExtractor={(item) => item.id}
                        contentContainerStyle={styles.listContent}
                        renderItem={({ item }) => <PlaceCard place={item} />}
                        showsVerticalScrollIndicator={false}
                    />
                )}
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        paddingHorizontal: spacing.lg,
        paddingTop: spacing.lg,
    },

    header: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: spacing.md,
    },

    backText: {
        color: colors.textPrimary,
        fontSize: 16,
    },

    title: {
        color: colors.textPrimary,
        fontSize: 20,
        fontWeight: '800',
    },

    filtersCard: {
        borderRadius: 16,
        padding: spacing.md,
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.08)',
        marginBottom: spacing.md,
    },

    sectionTitle: {
        color: colors.textPrimary,
        fontSize: 14,
        fontWeight: '800',
        marginBottom: spacing.sm,
    },

    filterRow: {
        marginTop: spacing.sm,
    },

    label: {
        color: colors.textSecondary,
        fontSize: 12,
        marginBottom: spacing.xs,
    },

    input: {
        height: 42,
        borderRadius: 12,
        paddingHorizontal: spacing.md,
        color: colors.textPrimary,
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.10)',
    },

    statusRow: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginTop: spacing.md,
    },

    statusText: {
        color: colors.textSecondary,
        fontSize: 12,
    },

    refreshBtn: {
        paddingHorizontal: spacing.md,
        paddingVertical: spacing.xs,
        borderRadius: 999,
        backgroundColor: 'rgba(52, 211, 153, 0.15)',
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },

    refreshText: {
        color: colors.primary,
        fontSize: 12,
        fontWeight: '800',
    },

    center: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: spacing.lg,
    },

    loadingText: {
        color: colors.textPrimary,
        marginTop: spacing.sm,
    },

    errorTitle: {
        color: colors.textPrimary,
        fontSize: 16,
        fontWeight: '800',
        marginBottom: spacing.xs,
        textAlign: 'center',
    },

    errorText: {
        color: '#F97373',
        textAlign: 'center',
    },

    retryBtn: {
        marginTop: spacing.md,
        paddingHorizontal: spacing.lg,
        paddingVertical: spacing.sm,
        borderRadius: 999,
        backgroundColor: 'rgba(249, 115, 115, 0.15)',
        borderWidth: 1,
        borderColor: 'rgba(249, 115, 115, 0.35)',
    },

    retryText: {
        color: '#F97373',
        fontWeight: '800',
    },

    emptyTitle: {
        color: colors.textPrimary,
        fontSize: 16,
        fontWeight: '800',
        textAlign: 'center',
        marginBottom: spacing.xs,
    },

    emptyText: {
        color: colors.textSecondary,
        textAlign: 'center',
    },

    listContent: {
        paddingBottom: spacing.xl,
    },
});

export default PlacesListScreen;
