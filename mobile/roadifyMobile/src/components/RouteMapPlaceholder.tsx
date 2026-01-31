import React, { useEffect, useRef } from 'react';
import { Animated, StyleSheet, Text, View } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { getTextStyle, theme } from '../theme/theme';

type Props = {
    fromCity?: string;
    toCity?: string;
    height?: number;
    showHeader?: boolean;
    shimmerEnabled?: boolean; // ✅ NEW
};

export const RouteMapPlaceholder: React.FC<Props> = ({
                                                         fromCity,
                                                         toCity,
                                                         height = 420,
                                                         showHeader = true,
                                                         shimmerEnabled = false, // ✅ default OFF (stable)
                                                     }) => {
    const shimmer = useRef(new Animated.Value(0)).current;

    useEffect(() => {
        if (!shimmerEnabled) return;

        const loop = Animated.loop(
            Animated.sequence([
                Animated.timing(shimmer, { toValue: 1, duration: 1400, useNativeDriver: true }),
                Animated.timing(shimmer, { toValue: 0, duration: 1400, useNativeDriver: true }),
            ])
        );
        loop.start();
        return () => loop.stop();
    }, [shimmer, shimmerEnabled]);

    const translateX = shimmer.interpolate({
        inputRange: [0, 1],
        outputRange: [-180, 260],
    });

    const subtitle = fromCity && toCity ? `${fromCity} → ${toCity}` : 'Map will appear here';

    return (
        <View style={[styles.container, { height }]}>
            <LinearGradient colors={[theme.colors.bg, theme.colors.surface]} style={StyleSheet.absoluteFill} />

            {/* ✅ Render shimmer ONLY when enabled */}
            {shimmerEnabled ? (
                <Animated.View
                    pointerEvents="none"
                    style={[styles.sheenWrap, { transform: [{ translateX }, { rotateZ: '-10deg' }] }]}
                >
                    <LinearGradient
                        colors={['rgba(255,255,255,0.00)', 'rgba(255,255,255,0.10)', 'rgba(255,255,255,0.00)']}
                        style={StyleSheet.absoluteFill}
                        start={{ x: 0, y: 0 }}
                        end={{ x: 1, y: 0 }}
                    />
                </Animated.View>
            ) : null}

            <View style={styles.routeArt}>
                <View style={styles.dot} />
                <View style={styles.line} />
                <View style={[styles.dot, styles.dotEnd]} />
            </View>

            {showHeader ? (
                <View style={styles.caption}>
                    <Text style={styles.captionTitle}>Route Preview</Text>
                    <Text style={styles.captionSub}>{subtitle}</Text>
                </View>
            ) : (
                <View style={styles.captionCompact}>
                    <Text style={styles.captionSub}>{subtitle}</Text>
                </View>
            )}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        width: '100%',
        borderBottomWidth: 1,
        borderBottomColor: theme.colors.border,
        overflow: 'hidden',
        backgroundColor: theme.colors.surface,
    },
    sheenWrap: {
        position: 'absolute',
        top: -80,
        bottom: -80,
        width: 110,
        left: 0,
        opacity: 0.9,
    },
    routeArt: {
        position: 'absolute',
        left: theme.spacing.lg,
        bottom: theme.spacing.lg,
        flexDirection: 'row',
        alignItems: 'center',
        gap: 10,
        opacity: 0.9,
    },
    dot: {
        width: 10,
        height: 10,
        borderRadius: 999,
        backgroundColor: theme.colors.primary,
        borderWidth: 2,
        borderColor: 'rgba(2,6,23,0.55)',
    },
    dotEnd: {
        backgroundColor: 'rgba(249,250,251,0.85)',
        borderColor: 'rgba(52,211,153,0.45)',
    },
    line: {
        width: 140,
        height: 3,
        borderRadius: 999,
        backgroundColor: 'rgba(52, 211, 153, 0.35)',
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.45)',
    },
    caption: {
        position: 'absolute',
        top: theme.spacing.lg,
        left: theme.spacing.lg,
        right: theme.spacing.lg,
    },
    captionCompact: {
        position: 'absolute',
        top: theme.spacing.lg + 44,
        left: theme.spacing.lg,
        right: theme.spacing.lg,
    },
    captionTitle: {
        color: theme.colors.text,
        ...getTextStyle('h1'),
    },
    captionSub: {
        marginTop: theme.spacing.xs,
        color: theme.colors.textMuted,
        ...getTextStyle('body'),
    },
});
