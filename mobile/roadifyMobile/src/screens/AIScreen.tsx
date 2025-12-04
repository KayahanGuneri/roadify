import React, { useRef, useEffect } from 'react';
import {
    View,
    Text,
    StyleSheet,
    Image,
    Animated,
    ScrollView,
} from 'react-native';
import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';

const hero = require('../assets/icons/ai.png');

export const AIScreen: React.FC<any> = ({ navigation }) => {
    const fade = useRef(new Animated.Value(0)).current;
    const translateY = useRef(new Animated.Value(16)).current;

    useEffect(() => {
        Animated.parallel([
            Animated.timing(fade, { toValue: 1, duration: 600, useNativeDriver: true }),
            Animated.timing(translateY, {
                toValue: 0,
                duration: 600,
                useNativeDriver: true,
            }),
        ]).start();
    }, [fade, translateY]);

    return (
        <Screen>
            <ScrollView contentContainerStyle={styles.container}>
                <Animated.View
                    style={[
                        styles.card,
                        { opacity: fade, transform: [{ translateY }] },
                    ]}
                >
                    <Text style={styles.title}>AI-powered recommendations</Text>
                    <Text style={styles.subtitle}>
                        Roadify analyses your route and suggests the best cafes, view points
                        and fuel stops — tailored to you.
                    </Text>

                    <Image source={hero} style={styles.image} resizeMode="contain" />

                    <Text style={styles.helper}>
                        In the next phases, this screen will connect to the AI
                        microservice and show real suggestions for your trips.
                    </Text>

                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: 16 }}
                    />
                </Animated.View>
            </ScrollView>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: {
        padding: 16,
        paddingBottom: 32,
    },
    card: {
        backgroundColor: 'rgba(15,23,42,0.96)',
        borderRadius: 24,
        padding: 20,
    },
    title: {
        color: '#FFFFFF',
        fontSize: 22,
        fontWeight: '700',
        marginBottom: 4,
    },
    subtitle: {
        color: '#9CA3AF',
        fontSize: 14,
        marginBottom: 16,
    },
    image: {
        width: '100%',
        height: 260,
        borderRadius: 24,
        marginBottom: 16,
    },
    helper: {
        color: '#9CA3AF',
        fontSize: 13,
        lineHeight: 18,
    },
});
