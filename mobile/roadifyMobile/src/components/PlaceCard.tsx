import React, { useMemo } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import type { PlaceDTO } from '../types/places';
import { theme, getTextStyle } from '../theme/theme';
import { PressableScale } from './PressableScale';

type Props = {
    place: PlaceDTO;
    onAddToTrip?: (place: PlaceDTO) => void;
};

export const PlaceCard: React.FC<Props> = ({ place, onAddToTrip }) => {
    const name = useMemo(() => {
        const raw = place.name?.trim();
        return raw ? raw : 'Unnamed place';
    }, [place.name]);

    const subtitle = !place.name?.trim() ? 'Provider did not return a name' : null;

    const ratingText = place.rating === null ? '—' : place.rating.toFixed(1);
    const detourText = Number.isFinite(place.detourKm) ? `${place.detourKm.toFixed(1)} km detour` : '—';

    return (
        <View style={styles.card}>
            <View style={styles.headerRow}>
                <View style={styles.titleCol}>
                    <Text style={styles.name} numberOfLines={2}>
                        {name}
                    </Text>
                    {subtitle ? (
                        <Text style={styles.subtitle} numberOfLines={1}>
                            {subtitle}
                        </Text>
                    ) : null}
                </View>

                <View style={styles.badge}>
                    <Text style={styles.badgeText}>{place.category}</Text>
                </View>
            </View>

            <View style={styles.metaRow}>
                <Text style={styles.metaText}>Rating {ratingText}</Text>
                <Text style={styles.dot}>•</Text>
                <Text style={styles.metaText}>{detourText}</Text>
            </View>

            {onAddToTrip ? (
                <PressableScale
                    onPress={() => onAddToTrip(place)}
                    contentStyle={styles.addBtn}
                    hitSlop={{ top: 6, bottom: 6, left: 6, right: 6 }}
                >
                    <Text style={styles.addBtnText}>Add to trip</Text>
                </PressableScale>
            ) : null}
        </View>
    );
};

const styles = StyleSheet.create({
    card: {
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderRadius: theme.radius.lg,
        padding: theme.spacing.md,
        borderWidth: 1,
        borderColor: theme.colors.border,
        marginBottom: theme.spacing.sm,
    },
    headerRow: {
        flexDirection: 'row',
        alignItems: 'flex-start',
        justifyContent: 'space-between',
        gap: theme.spacing.sm,
    },
    titleCol: { flex: 1 },
    name: {
        color: theme.colors.text,
        ...getTextStyle('bodyMedium'),
    },
    subtitle: {
        marginTop: 2,
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },
    badge: {
        paddingHorizontal: theme.spacing.sm,
        paddingVertical: theme.spacing.xs,
        borderRadius: theme.radius.pill,
        backgroundColor: theme.colors.primarySoft,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },
    badgeText: {
        color: theme.colors.primary,
        ...getTextStyle('overline'),
    },
    metaRow: {
        flexDirection: 'row',
        alignItems: 'center',
        marginTop: theme.spacing.sm,
    },
    metaText: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },
    dot: {
        color: theme.colors.textMuted,
        marginHorizontal: 8,
        ...getTextStyle('caption'),
    },
    addBtn: {
        marginTop: theme.spacing.md,
        paddingVertical: theme.spacing.sm,
        borderRadius: theme.radius.md,
        alignItems: 'center',
        backgroundColor: theme.colors.primarySoft,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },
    addBtnText: {
        color: theme.colors.primary,
        ...getTextStyle('bodyMedium'),
    },
});
