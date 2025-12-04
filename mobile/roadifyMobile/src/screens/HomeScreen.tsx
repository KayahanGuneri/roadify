// src/screens/HomeScreen.tsx
import React from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { RootStackParamList } from '../navigation/RootStack';

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

export const HomeScreen: React.FC<Props> = ({ navigation }) => {
    return (
        <Screen>
            {/* Top hero area */}
            <View style={styles.header}>
                <View style={styles.logoRow}>
                    <Image
                        // LOGO BURADA
                        source={require('../assets/logos/app-icon.png')}
                        // istersen roadify-logo.png da kullanabilirsin:
                        // source={require('../assets/logos/roadify-logo.png')}
                        style={styles.logo}
                    />
                    <View>
                        <Text style={styles.appName}>Roadify</Text>
                        <Text style={styles.appSubtitle}>Smart Navigation</Text>
                    </View>
                </View>

                {/* HERO GÖRSELİ */}
                <Image
                    source={require('../assets/illustrations/hero.png')}
                    style={styles.heroImage}
                    resizeMode="contain"
                />
            </View>

            {/* Main action card */}
            <View style={styles.card}>
                <Text style={styles.cardTitle}>Get started</Text>
                <Text style={styles.cardSubtitle}>
                    Plan your next road trip with AI-powered suggestions.
                </Text>

                <PrimaryButton
                    title="Plan Trip"
                    onPress={() => navigation.navigate('RouteSelection')}
                    style={{ marginTop: 16 }}
                />
                <PrimaryButton
                    title="AI Assistant"
                    onPress={() => navigation.navigate('AIScreen')}
                    style={{ marginTop: 12 }}
                />
                <PrimaryButton
                    title="Analytics"
                    onPress={() => navigation.navigate('AnalyticsScreen')}
                    style={{ marginTop: 12 }}
                />
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    header: {
        marginBottom: 16,
    },
    logoRow: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    logo: {
        width: 40,
        height: 40,
        borderRadius: 12,
        marginRight: 10,
    },
    appName: {
        color: '#FFFFFF',
        fontSize: 22,
        fontWeight: '800',
    },
    appSubtitle: {
        color: '#A7F3D0',
        fontSize: 12,
    },
    heroImage: {
        marginTop: 16,
        width: '100%',
        height: 160,
        borderRadius: 24,
    },
    card: {
        backgroundColor: 'rgba(15,23,42,0.96)',
        borderRadius: 24,
        padding: 20,
    },
    cardTitle: {
        color: '#FFFFFF',
        fontSize: 18,
        fontWeight: '700',
    },
    cardSubtitle: {
        color: '#9CA3AF',
        marginTop: 4,
        marginBottom: 4,
    },
});
