import React from 'react';
import { StyleSheet, Text, TextInput, View } from 'react-native';
import { getTextStyle, theme } from '../theme/theme';

type Props = {
    label: string;
    value: string;
    onChangeText: (v: string) => void;
    placeholder?: string;
    helperText?: string;
};

export const NumberField: React.FC<Props> = ({ label, value, onChangeText, placeholder, helperText }) => {
    return (
        <View style={styles.root}>
            <Text style={styles.label}>{label}</Text>

            <TextInput
                value={value}
                onChangeText={(t) => onChangeText(t.replace(',', '.'))}
                placeholder={placeholder}
                placeholderTextColor="rgba(255,255,255,0.35)"
                keyboardType="numeric"
                style={styles.input}
            />

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
    input: {
        height: 42,
        borderRadius: theme.radius.md,
        paddingHorizontal: theme.spacing.md,
        color: theme.colors.text,
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: theme.colors.border,
    },
    helper: {
        marginTop: theme.spacing.xs,
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },
});
