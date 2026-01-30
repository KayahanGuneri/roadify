// src/components/PressableScale.tsx
import React, { useMemo, useRef } from 'react';
import { Animated, Pressable, StyleProp, ViewStyle } from 'react-native';

type Props = {
    children: React.ReactNode;
    onPress?: () => void;
    disabled?: boolean;
    style?: StyleProp<ViewStyle>;        // wrapper style (includes transform)
    contentStyle?: StyleProp<ViewStyle>; // style applied to Pressable
    hitSlop?: { top?: number; bottom?: number; left?: number; right?: number };
    testID?: string;
};

export const PressableScale: React.FC<Props> = ({
                                                    children,
                                                    onPress,
                                                    disabled,
                                                    style,
                                                    contentStyle,
                                                    hitSlop,
                                                    testID,
                                                }) => {
    const scale = useRef(new Animated.Value(1)).current;

    const pressIn = () => {
        if (disabled) return;
        Animated.spring(scale, {
            toValue: 0.97,
            useNativeDriver: true,
            speed: 32,
            bounciness: 0,
        }).start();
    };

    const pressOut = () => {
        if (disabled) return;
        Animated.spring(scale, {
            toValue: 1,
            useNativeDriver: true,
            speed: 32,
            bounciness: 0,
        }).start();
    };

    const mergedWrapperStyle = useMemo(
        () => [{ transform: [{ scale }] }, style] as any,
        [scale, style]
    );

    return (
        <Animated.View style={mergedWrapperStyle}>
            <Pressable
                testID={testID}
                hitSlop={hitSlop}
                onPress={disabled ? undefined : onPress}
                onPressIn={pressIn}
                onPressOut={pressOut}
                style={({ pressed }) => [
                    contentStyle,
                    disabled ? { opacity: 0.55 } : null,
                    pressed && !disabled ? { opacity: 0.96 } : null,
                ]}
            >
                {children}
            </Pressable>
        </Animated.View>
    );
};
