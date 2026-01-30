import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import LottieView from 'lottie-react-native';

import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { AppBar } from '../components/AppBar';
import type { RootStackParamList } from '../navigation/types';
import { getTextStyle, theme } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'RouteMapFull'>;

export const RouteMapFullScreen: React.FC<Props> = ({ navigation, route }) => {
    return (
        <Screen>
            <AppBar title="Route Map" onBack={() => navigation.goBack()} />

            <Text style={styles.subtitle}>
                We’re preparing a rich, interactive map experience for this route.
            </Text>

            <View style={styles.center}>
                <LottieView source={require('../assets/animations/car.json')} autoPlay loop style={styles.lottie} />
                <Text style={styles.caption}>Route ID: {route.params.routeId}</Text>
            </View>

            <PrimaryButton title="Back to Preview" onPress={() => navigation.goBack()} />
        </Screen>
    );
};

const styles = StyleSheet.create({
    subtitle: {
        color: theme.colors.textMuted,
        ...getTextStyle('body'),
        marginTop: theme.spacing.sm,
        marginBottom: theme.spacing.md,
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
        color: theme.colors.primary,
        ...getTextStyle('caption'),
        marginTop: theme.spacing.md,
    },
});

export default RouteMapFullScreen;
