import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import type { PlaceDTO } from '../types/places';
import { colors, spacing } from '../theme/theme';

type Props = {
    place: PlaceDTO;
};

/**
 * PlaceCard
 *
 * English:
 * Small reusable card for displaying a single place.
 *
 * Türkçe Özet:
 * Tek bir mekanı liste içinde göstermek için tekrar kullanılabilir kart.
 */
export const PlaceCard: React.FC<Props> = ({ place }) => {
    const ratingText =
        place.rating === null ? '—' : place.rating.toFixed(1);

    return (
        <View style={styles.card}>
            <View style={styles.headerRow}>
                <Text style={styles.name} numberOfLines={2}>
                    {place.name}
                </Text>

                <View style={styles.badge}>
                    <Text style={styles.badgeText}>{place.category}</Text>
                </View>
            </View>

            <View style={styles.metaRow}>
                <Text style={styles.metaText}>Rating: {ratingText}</Text>
                <Text style={styles.metaText}>
                    Detour: {place.detourKm.toFixed(1)} km
                </Text>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    card: {
        backgroundColor: colors.background,
        borderRadius: 16,
        padding: spacing.md,
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.08)',
        marginBottom: spacing.sm,
    },
    headerRow: {
        flexDirection: 'row',
        alignItems: 'flex-start',
        justifyContent: 'space-between',
        gap: spacing.sm,
    },
    name: {
        flex: 1,
        color: colors.textPrimary,
        fontSize: 16,
        fontWeight: '700',
    },
    badge: {
        paddingHorizontal: spacing.sm,
        paddingVertical: spacing.xs,
        borderRadius: 999,
        backgroundColor: 'rgba(52, 211, 153, 0.15)',
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },
    badgeText: {
        color: colors.primary,
        fontSize: 11,
        fontWeight: '700',
    },
    metaRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: spacing.sm,
    },
    metaText: {
        color: colors.textSecondary,
        fontSize: 12,
    },
});
