// src/components/Screen.tsx
import React, { useEffect, useRef } from 'react';
import { Animated, StyleSheet, StatusBar } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

type Props = {
    children: React.ReactNode;
};

export const Screen: React.FC<Props> = ({ children }) => {
    const opacity = useRef(new Animated.Value(0)).current;
    const translateY = useRef(new Animated.Value(20)).current;

    useEffect(() => {
        Animated.parallel([
            Animated.timing(opacity, {
                toValue: 1,
                duration: 400,
                useNativeDriver: true,
            }),
            Animated.timing(translateY, {
                toValue: 0,
                duration: 400,
                useNativeDriver: true,
            }),
        ]).start();
    }, [opacity, translateY]);

    return (
        <LinearGradient
            colors={['#0F1C2D', '#34D399']}
            style={styles.gradient}
        >
            <StatusBar barStyle="light-content" />
            <Animated.View
                style={[
                    styles.content,
                    { opacity, transform: [{ translateY }] },
                ]}
            >
                {children}
            </Animated.View>
        </LinearGradient>
    );
};

const styles = StyleSheet.create({
    gradient: {
        flex: 1,
    },
    content: {
        flex: 1,
        paddingHorizontal: 20,
        paddingTop: 24,
        paddingBottom: 16,
    },
});
