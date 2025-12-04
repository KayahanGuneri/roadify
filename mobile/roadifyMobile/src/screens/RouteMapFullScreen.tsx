// src/screens/RouteMapFullScreen.tsx
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import LottieView from 'lottie-react-native';

import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { RootStackParamList } from '../navigation/RootStack';

type Props = NativeStackScreenProps<RootStackParamList, 'RouteMapFull'>;

export const RouteMapFullScreen: React.FC<Props> = ({ navigation, route }) => {
    return (
        <Screen>
            <Text style={styles.title}>Full Route Map</Text>
            <Text style={styles.subtitle}>
                We’re preparing a rich, interactive map experience for this route.
            </Text>

            <View style={styles.center}>
                <LottieView
                    source={require('../assets/animations/car.json')}
                    autoPlay
                    loop
                    style={styles.lottie}
                />
                <Text style={styles.caption}>Route ID: {route.params.routeId}</Text>
            </View>

            <PrimaryButton
                title="Back to Preview"
                onPress={() => navigation.goBack()}
            />
        </Screen>
    );
};

const styles = StyleSheet.create({
    title: {
        color: '#FFFFFF',
        fontSize: 20,
        fontWeight: '700',
        marginBottom: 4,
    },
    subtitle: {
        color: '#9CA3AF',
        marginBottom: 10,
    },
    center: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    lottie: {
        width: 260,
        height: 260,
    },
    caption: {
        color: '#A7F3D0',
        marginTop: 10,
    },
});
