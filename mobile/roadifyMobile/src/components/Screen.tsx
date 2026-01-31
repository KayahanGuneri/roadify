import React, { useEffect, useRef } from 'react';
import { Animated, StyleSheet, StatusBar, StyleProp, ViewStyle } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { theme } from '../theme/theme';
import { AnimatedBackground } from './AnimatedBackground';

type Props = {
    children: React.ReactNode;
    style?: StyleProp<ViewStyle>;        // outer container
    contentStyle?: StyleProp<ViewStyle>; // inner content wrapper
    noPadding?: boolean;

    /**
     * English: Optional background mode.
     * Türkçe: Faz 4 için sadece Home/Auth ekranlarında living background açacağız.
     */
    background?: 'none' | 'living';
};

export const Screen: React.FC<Props> = ({
                                            children,
                                            style,
                                            contentStyle,
                                            noPadding,
                                            background = 'none',
                                        }) => {
    const opacity = useRef(new Animated.Value(0)).current;
    const translateY = useRef(new Animated.Value(14)).current;

    useEffect(() => {
        Animated.parallel([
            Animated.timing(opacity, { toValue: 1, duration: 380, useNativeDriver: true }),
            Animated.timing(translateY, { toValue: 0, duration: 380, useNativeDriver: true }),
        ]).start();
    }, [opacity, translateY]);

    return (
        <LinearGradient colors={[theme.colors.bg, theme.colors.surface]} style={[styles.gradient, style]}>
            <StatusBar barStyle="light-content" />

            {background === 'living' ? <AnimatedBackground /> : null}

            <Animated.View
                style={[
                    styles.content,
                    noPadding ? styles.noPadding : styles.padded,
                    { opacity, transform: [{ translateY }] },
                    contentStyle,
                ]}
            >
                {children}
            </Animated.View>
        </LinearGradient>
    );
};

const styles = StyleSheet.create({
    gradient: { flex: 1 },
    content: { flex: 1 },
    padded: {
        paddingHorizontal: theme.spacing.lg,
        paddingTop: theme.spacing.lg,
        paddingBottom: theme.spacing.md,
    },
    noPadding: {
        paddingHorizontal: 0,
        paddingTop: 0,
        paddingBottom: 0,
    },
});
