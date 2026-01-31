import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { PressableScale } from './PressableScale';
import { getTextStyle, theme } from '../theme/theme';

type Props = {
    label?: string;
    options: string[];
    value: string | null;
    onChange: (next: string | null) => void;
    helperText?: string;
};

export const FilterChips: React.FC<Props> = ({ label, options, value, onChange, helperText }) => {
    return (
        <View style={styles.root}>
            {label ? <Text style={styles.label}>{label}</Text> : null}

            <View style={styles.row}>
                {options.map((opt) => {
                    const selected = value === opt;
                    return (
                        <PressableScale
                            key={opt}
                            onPress={() => onChange(selected ? null : opt)}
                            contentStyle={[styles.chip, selected ? styles.chipActive : styles.chipIdle]}
                            hitSlop={{ top: 6, bottom: 6, left: 6, right: 6 }}
                        >
                            <Text style={[styles.chipText, selected ? styles.chipTextActive : styles.chipTextIdle]}>
                                {opt}
                            </Text>
                        </PressableScale>
                    );
                })}
            </View>

            {helperText ? <Text style={styles.helper}>{helperText}</Text> : null}
        </View>
    );
};

const styles = StyleSheet.create({
    root: { marginTop: theme.spacing.sm },
    label: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
        marginBottom: theme.spacing.xs,
    },
    row: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        gap: theme.spacing.sm,
    },
    chip: {
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.xs,
        borderRadius: theme.radius.pill,
        borderWidth: 1,
    },
    chipIdle: {
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderColor: theme.colors.border,
    },
    chipActive: {
        backgroundColor: theme.colors.primarySoft,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },
    chipText: { ...getTextStyle('overline') },
    chipTextIdle: { color: theme.colors.textMuted },
    chipTextActive: { color: theme.colors.primary },
    helper: {
        marginTop: theme.spacing.xs,
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },
});
