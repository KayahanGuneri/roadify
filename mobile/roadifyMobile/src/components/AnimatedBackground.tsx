// src/components/AnimatedBackground.tsx

/**
 * AnimatedBackground.tsx
 *
 * English:
 * Subtle "living" background with slow drifting blobs.
 * Performance-friendly: transform-only animation, native driver enabled.
 *
 * Türkçe Özet:
 * Yavaş kayan/ölçeklenen bloblarla premium his veren arka plan.
 * Sadece transform animasyonu (useNativeDriver) => performans dostu.
 */

import React, { useEffect, useMemo, useRef } from 'react';
import { Animated, StyleSheet, ViewStyle } from 'react-native';
import { theme } from '../theme/theme';

type Props = {
    enabled?: boolean;
    style?: ViewStyle;
};

export const AnimatedBackground: React.FC<Props> = ({ enabled = true, style }) => {
    const t = useRef(new Animated.Value(0)).current;

    useEffect(() => {
        if (!enabled) return;

        const loop = Animated.loop(
            Animated.timing(t, {
                toValue: 1,
                duration: 11000, // 8–12s bandında
                useNativeDriver: true,
            })
        );

        loop.start();
        return () => loop.stop();
    }, [enabled, t]);

    const anim = useMemo(() => {
        // 0..1 => drift and scale
        const driftX = t.interpolate({ inputRange: [0, 1], outputRange: [-14, 14] });
        const driftY = t.interpolate({ inputRange: [0, 1], outputRange: [10, -10] });
        const scale = t.interpolate({ inputRange: [0, 1], outputRange: [1.02, 1.08] });

        // ikinci blob için farklı faz hissi (ters yön)
        const driftX2 = t.interpolate({ inputRange: [0, 1], outputRange: [12, -12] });
        const driftY2 = t.interpolate({ inputRange: [0, 1], outputRange: [-8, 8] });
        const scale2 = t.interpolate({ inputRange: [0, 1], outputRange: [1.06, 1.01] });

        // üçüncü daha minimal
        const driftY3 = t.interpolate({ inputRange: [0, 1], outputRange: [8, -6] });
        const scale3 = t.interpolate({ inputRange: [0, 1], outputRange: [1.0, 1.05] });

        return { driftX, driftY, scale, driftX2, driftY2, scale2, driftY3, scale3 };
    }, [t]);

    if (!enabled) return null;

    return (
        <Animated.View pointerEvents="none" style={[styles.container, style]}>
            <Animated.View
                style={[
                    styles.blob,
                    styles.blob1,
                    {
                        transform: [
                            { translateX: anim.driftX },
                            { translateY: anim.driftY },
                            { scale: anim.scale },
                        ],
                    },
                ]}
            />
            <Animated.View
                style={[
                    styles.blob,
                    styles.blob2,
                    {
                        transform: [
                            { translateX: anim.driftX2 },
                            { translateY: anim.driftY2 },
                            { scale: anim.scale2 },
                        ],
                    },
                ]}
            />
            <Animated.View
                style={[
                    styles.blob,
                    styles.blob3,
                    {
                        transform: [{ translateY: anim.driftY3 }, { scale: anim.scale3 }],
                    },
                ]}
            />
        </Animated.View>
    );
};

const styles = StyleSheet.create({
    container: {
        ...StyleSheet.absoluteFillObject,
        overflow: 'hidden',
    },
    blob: {
        position: 'absolute',
        borderRadius: 999,
    },

    // Büyük, yumuşak yeşil glow
    blob1: {
        width: 340,
        height: 340,
        top: -140,
        left: -120,
        backgroundColor: theme.colors.primarySoft,
        opacity: 0.9,
    },

    // Daha “deep” bir gölge/glow
    blob2: {
        width: 420,
        height: 420,
        bottom: -220,
        right: -180,
        backgroundColor: 'rgba(15, 28, 45, 0.55)', // navy haze
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.05)',
        opacity: 1,
    },

    // Küçük highlight
    blob3: {
        width: 220,
        height: 220,
        top: 120,
        right: -90,
        backgroundColor: 'rgba(110, 231, 183, 0.10)', // lightGreen very soft
        opacity: 1,
    },
});
