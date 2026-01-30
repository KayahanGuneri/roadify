import React, { useMemo } from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PrimaryButton } from '../components/PrimaryButton';
import type { RootStackParamList } from '../navigation/types';
import { useAuth } from '../context/AuthContext';
import { theme } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

export const HomeScreen: React.FC<Props> = ({ navigation }) => {
    const { isLoggedIn, logout } = useAuth();
    const authed = useMemo(() => Boolean(isLoggedIn), [isLoggedIn]);

    return (
        <Screen>
            <AppBar title="Roadify" />

            <View style={styles.header}>
                <View style={styles.logoRow}>
                    <Image source={require('../assets/logos/app-icon.png')} style={styles.logo} />
                    <View>
                        <Text style={styles.appName}>Roadify</Text>
                        <Text style={styles.appSubtitle}>Plan smarter road trips</Text>
                    </View>
                </View>

                <Image
                    source={require('../assets/illustrations/hero.png')}
                    style={styles.heroImage}
                    resizeMode="contain"
                />
            </View>

            <View style={styles.card}>
                {!authed ? (
                    <>
                        <Text style={styles.cardTitle}>Welcome</Text>
                        <Text style={styles.cardSubtitle}>
                            Login to build trips, save stops, and use personalized features.
                        </Text>

                        <PrimaryButton
                            title="Login"
                            onPress={() => navigation.navigate('Login')}
                            style={{ marginTop: 16 }}
                        />

                        <PrimaryButton
                            title="Create account"
                            onPress={() => navigation.navigate('Register')}
                            style={{ marginTop: 12 }}
                        />

                        <View style={styles.divider} />

                        <Text style={styles.cardSubtitle}>
                            You can still explore basic screens without login.
                        </Text>

                        <PrimaryButton
                            title="Explore (Plan Trip)"
                            onPress={() => navigation.navigate('RouteSelection')}
                            style={{ marginTop: 12 }}
                        />
                    </>
                ) : (
                    <>
                        <Text style={styles.cardTitle}>Dashboard</Text>
                        <Text style={styles.cardSubtitle}>Continue planning your next trip.</Text>

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
                        <PrimaryButton
                            title="Trip Planner"
                            onPress={() => navigation.navigate('TripPlanner')}
                            style={{ marginTop: 12 }}
                        />

                        <View style={styles.divider} />

                        <PrimaryButton title="Logout" onPress={() => void logout()} />
                    </>
                )}
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    header: { marginBottom: 16, marginTop: theme.spacing.md },
    logoRow: { flexDirection: 'row', alignItems: 'center' },
    logo: { width: 40, height: 40, borderRadius: 12, marginRight: 10 },
    appName: { color: '#FFFFFF', fontSize: 22, fontWeight: '800' },
    appSubtitle: { color: '#A7F3D0', fontSize: 12 },
    heroImage: { marginTop: 16, width: '100%', height: 160, borderRadius: 24 },

    card: {
        backgroundColor: 'rgba(15,23,42,0.96)',
        borderRadius: 24,
        padding: 20,
    },
    cardTitle: { color: '#FFFFFF', fontSize: 18, fontWeight: '700' },
    cardSubtitle: { color: '#9CA3AF', marginTop: 6, lineHeight: 18 },

    divider: {
        height: 1,
        backgroundColor: 'rgba(255,255,255,0.10)',
        marginVertical: 16,
    },
});

export default HomeScreen;
