import React, { useMemo } from 'react';
import { StyleSheet, Text, View, ViewStyle } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { theme, getTextStyle } from '../theme/theme';
import { PressableScale } from './PressableScale';

type RightAction =
    | { label: string; onPress: () => void; disabled?: boolean }
    | React.ReactNode
    | null
    | undefined;

type Props = {
    title: string;
    onBack?: () => void;
    right?: RightAction;
    variant?: 'default' | 'overlay';
    style?: ViewStyle;
};

function isLabelAction(value: RightAction): value is { label: string; onPress: () => void; disabled?: boolean } {
    return !!value && typeof value === 'object' && 'label' in value && 'onPress' in value;
}

export const AppBar: React.FC<Props> = ({ title, onBack, right, variant = 'default', style }) => {
    const navigation = useNavigation<any>();

    const canGoBack = navigation.canGoBack?.() ?? false;
    const backHandler = useMemo(() => {
        if (onBack) return onBack;
        if (!canGoBack) return undefined;
        return () => navigation.goBack();
    }, [canGoBack, navigation, onBack]);

    return (
        <View style={[styles.container, variant === 'overlay' ? styles.overlay : styles.default, style]}>
            <View style={styles.left}>
                {backHandler ? (
                    <PressableScale
                        onPress={backHandler}
                        hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
                        contentStyle={styles.backBtn}
                        testID="appbar-back"
                    >
                        <Text style={styles.backText}>‹</Text>
                    </PressableScale>
                ) : (
                    <View style={styles.leftSpacer} />
                )}
            </View>

            <Text style={styles.title} numberOfLines={1}>
                {title}
            </Text>

            <View style={styles.right}>
                {isLabelAction(right) ? (
                    <PressableScale
                        onPress={right.disabled ? undefined : right.onPress}
                        disabled={right.disabled}
                        contentStyle={styles.rightBtn}
                        testID="appbar-right"
                    >
                        <Text style={styles.rightText}>{right.label}</Text>
                    </PressableScale>
                ) : right ? (
                    right
                ) : (
                    <View style={styles.rightSpacer} />
                )}
            </View>
        </View>
    );
};

const APPBAR_HEIGHT = 52;

const styles = StyleSheet.create({
    container: {
        height: APPBAR_HEIGHT,
        flexDirection: 'row',
        alignItems: 'center',
        paddingHorizontal: theme.spacing.lg,
        borderBottomWidth: 1,
        borderBottomColor: theme.colors.border,
    },
    default: {
        backgroundColor: 'rgba(2, 6, 23, 0.35)',
    },
    overlay: {
        position: 'absolute',
        top: theme.spacing.lg,
        left: 0,
        right: 0,
        borderBottomWidth: 0,
        backgroundColor: 'transparent',
        paddingHorizontal: theme.spacing.lg,
    },

    left: { width: 56, alignItems: 'flex-start' },
    right: { width: 56, alignItems: 'flex-end' },
    leftSpacer: { width: 24, height: 24 },
    rightSpacer: { width: 24, height: 24 },

    backBtn: {
        width: 36,
        height: 36,
        borderRadius: theme.radius.pill,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderWidth: 1,
        borderColor: theme.colors.border,
    },
    backText: {
        color: theme.colors.text,
        fontSize: 18,
        fontWeight: '800',
        marginTop: -2,
    },
    title: {
        flex: 1,
        textAlign: 'center',
        color: theme.colors.text,
        ...getTextStyle('h2'),
    },

    rightBtn: {
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.xs,
        borderRadius: theme.radius.pill,
        backgroundColor: theme.colors.primarySoft,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },
    rightText: {
        color: theme.colors.primary,
        ...getTextStyle('overline'),
    },
});
