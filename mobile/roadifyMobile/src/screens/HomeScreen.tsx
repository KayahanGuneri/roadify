import React, { useMemo } from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PrimaryButton } from '../components/PrimaryButton';
import type { RootStackParamList } from '../navigation/types';
import { useAuth } from '../context/AuthContext';
import { theme, getTextStyle } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

export const HomeScreen: React.FC<Props> = ({ navigation }) => {
    const { isLoggedIn, logout } = useAuth();
    const authed = useMemo(() => Boolean(isLoggedIn), [isLoggedIn]);

    return (
        <Screen background="living">
            <AppBar title="Roadify" />

            <View style={styles.header}>
                <View style={styles.logoRow}>
                    <Image source={require('../assets/logos/app-icon.png')} style={styles.logo} />
                    <View>
                        <Text style={styles.appName}>Roadify</Text>
                        <Text style={styles.appSubtitle}>Plan smarter road trips</Text>
                    </View>
                </View>

                <Image source={require('../assets/illustrations/hero.png')} style={styles.heroImage} resizeMode="contain" />
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
                            style={styles.mt16}
                        />
                        <PrimaryButton
                            title="Create account"
                            onPress={() => navigation.navigate('Register')}
                            style={styles.mt12}
                        />

                        <View style={styles.divider} />

                        <Text style={styles.cardSubtitle}>You can still explore basic screens without login.</Text>
                        <PrimaryButton
                            title="Explore (Plan Trip)"
                            onPress={() => navigation.navigate('RouteSelection')}
                            style={styles.mt12}
                        />
                    </>
                ) : (
                    <>
                        <Text style={styles.cardTitle}>Dashboard</Text>
                        <Text style={styles.cardSubtitle}>Continue planning your next trip.</Text>

                        <PrimaryButton
                            title="Plan Trip"
                            onPress={() => navigation.navigate('RouteSelection')}
                            style={styles.mt16}
                        />
                        <PrimaryButton
                            title="AI Assistant"
                            onPress={() => navigation.navigate('AIScreen')}
                            style={styles.mt12}
                        />
                        <PrimaryButton
                            title="Analytics"
                            onPress={() => navigation.navigate('AnalyticsScreen')}
                            style={styles.mt12}
                        />
                        <PrimaryButton
                            title="Trip Planner"
                            onPress={() => navigation.navigate('TripPlanner')}
                            style={styles.mt12}
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
    header: { marginBottom: theme.spacing.md, marginTop: theme.spacing.md },
    logoRow: { flexDirection: 'row', alignItems: 'center' },
    logo: { width: 40, height: 40, borderRadius: theme.radius.md, marginRight: 10 },

    appName: { color: theme.colors.text, ...getTextStyle('h1') },
    appSubtitle: { color: theme.colors.primary, ...getTextStyle('caption') },

    heroImage: { marginTop: theme.spacing.md, width: '100%', height: 160, borderRadius: theme.radius['2xl'] },

    card: {
        backgroundColor: 'rgba(15,23,42,0.92)',
        borderRadius: theme.radius['2xl'],
        padding: theme.spacing.lg,
        borderWidth: 1,
        borderColor: theme.colors.border,
        ...theme.elevation.e1,
    },
    cardTitle: { color: theme.colors.text, ...getTextStyle('h2') },
    cardSubtitle: { color: theme.colors.textMuted, marginTop: 6, lineHeight: 18 },

    divider: {
        height: 1,
        backgroundColor: theme.colors.border,
        marginVertical: theme.spacing.md,
    },

    mt16: { marginTop: 16 },
    mt12: { marginTop: 12 },
});

export default HomeScreen;
