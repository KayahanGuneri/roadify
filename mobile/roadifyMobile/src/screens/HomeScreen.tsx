// src/screens/HomeScreen.tsx
import React, { useMemo, useState } from 'react';
import { View, Text, StyleSheet, Image, Alert, TextInput } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { RootStackParamList } from '../navigation/RootStack';
import { useAuth } from '../context/AuthContext';

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

/**
 * HomeScreen
 *
 * English:
 * Entry screen with quick navigation and a DEV login for Keycloak.
 *
 * Türkçe Özet:
 * Uygulamanın giriş ekranı. M4 için Keycloak token üretmek amacıyla geçici DEV login içerir.
 */
export const HomeScreen: React.FC<Props> = ({ navigation }) => {
    const { login, logout, accessToken } = useAuth();

    // Dev login inputs (defaults to your test user)
    const [username, setUsername] = useState('testuser');
    const [password, setPassword] = useState('test1234');

    const isAuthed = useMemo(() => Boolean(accessToken), [accessToken]);

    const onDevLogin = async () => {
        try {
            await login(username.trim(), password);
            Alert.alert('Login OK', 'Access token loaded.');
        } catch (e: any) {
            const msg =
                e?.response?.data?.error_description ||
                e?.response?.data?.error ||
                e?.message ||
                'Unknown error';
            Alert.alert('Login failed', msg);
        }
    };

    return (
        <Screen>
            {/* Top hero area */}
            <View style={styles.header}>
                <View style={styles.logoRow}>
                    <Image
                        source={require('../assets/logos/app-icon.png')}
                        style={styles.logo}
                    />
                    <View>
                        <Text style={styles.appName}>Roadify</Text>
                        <Text style={styles.appSubtitle}>Plan smarter road trips</Text>
                    </View>
                </View>

                {/* HERO image */}
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
                    Discover the best stops on your route and build a trip plan.
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

                {/* DEV Auth card (M4) */}
                <View style={styles.devCard}>
                    <View style={styles.devHeaderRow}>
                        <Text style={styles.devTitle}>DEV Auth (Keycloak)</Text>
                        <View style={[styles.statusPill, isAuthed ? styles.statusOk : styles.statusNo]}>
                            <Text style={styles.statusText}>{isAuthed ? 'TOKEN READY' : 'NOT LOGGED IN'}</Text>
                        </View>
                    </View>

                    {!isAuthed ? (
                        <>
                            <Text style={styles.devHint}>
                                Türkçe: M4 aşamasında trip endpointleri protected olduğu için token gerekiyor.
                                Bu geçici login ile token alıyoruz.
                            </Text>

                            <Text style={styles.inputLabel}>Username</Text>
                            <TextInput
                                value={username}
                                onChangeText={setUsername}
                                autoCapitalize="none"
                                autoCorrect={false}
                                placeholder="testuser"
                                placeholderTextColor="rgba(255,255,255,0.35)"
                                style={styles.input}
                            />

                            <Text style={styles.inputLabel}>Password</Text>
                            <TextInput
                                value={password}
                                onChangeText={setPassword}
                                secureTextEntry
                                autoCapitalize="none"
                                autoCorrect={false}
                                placeholder="test1234"
                                placeholderTextColor="rgba(255,255,255,0.35)"
                                style={styles.input}
                            />

                            <PrimaryButton
                                title="Login (dev)"
                                onPress={onDevLogin}
                                style={{ marginTop: 12 }}
                            />
                        </>
                    ) : (
                        <>
                            <Text style={styles.devHint}>
                                Token yüklendi. Artık TripPlanner ve updateStops çağrıları çalışabilir.
                            </Text>

                            <PrimaryButton
                                title="Open Trip Planner"
                                onPress={() => navigation.navigate('TripPlanner')}
                                style={{ marginTop: 12 }}
                            />

                            <PrimaryButton
                                title="Logout (dev)"
                                onPress={logout}
                                style={{ marginTop: 10 }}
                            />
                        </>
                    )}
                </View>
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

    devCard: {
        marginTop: 16,
        borderRadius: 18,
        padding: 14,
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.08)',
    },
    devHeaderRow: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: 10,
    },
    devTitle: {
        color: '#FFFFFF',
        fontWeight: '800',
        fontSize: 13,
    },
    statusPill: {
        paddingHorizontal: 10,
        paddingVertical: 6,
        borderRadius: 999,
        borderWidth: 1,
    },
    statusOk: {
        backgroundColor: 'rgba(52, 211, 153, 0.15)',
        borderColor: 'rgba(52, 211, 153, 0.35)',
    },
    statusNo: {
        backgroundColor: 'rgba(249, 115, 115, 0.15)',
        borderColor: 'rgba(249, 115, 115, 0.35)',
    },
    statusText: {
        color: '#FFFFFF',
        fontSize: 10,
        fontWeight: '800',
        letterSpacing: 0.4,
    },
    devHint: {
        color: '#9CA3AF',
        fontSize: 12,
        marginBottom: 10,
        lineHeight: 16,
    },
    inputLabel: {
        color: '#A7F3D0',
        fontSize: 11,
        fontWeight: '700',
        marginTop: 6,
        marginBottom: 6,
    },
    input: {
        height: 42,
        borderRadius: 12,
        paddingHorizontal: 12,
        color: '#FFFFFF',
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.10)',
    },
});

export default HomeScreen;
