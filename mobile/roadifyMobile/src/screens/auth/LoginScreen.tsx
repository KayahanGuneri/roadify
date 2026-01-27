import React, { useState } from 'react';
import { Alert, StyleSheet, Text, TextInput, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../../components/Screen';
import { PrimaryButton } from '../../components/PrimaryButton';
import type { RootStackParamList } from '../../navigation/types';
import { useAuth } from '../../context/AuthContext';
import { openForgotPassword } from '../../lib/keycloakLinks';

type Props = NativeStackScreenProps<RootStackParamList, 'Login'>;

export const LoginScreen: React.FC<Props> = ({ navigation }) => {
    const { login } = useAuth();

    const [username, setUsername] = useState('testuser');
    const [password, setPassword] = useState('test1234');
    const [loading, setLoading] = useState(false);

    const onLogin = async () => {
        try {
            setLoading(true);
            await login(username.trim(), password);
        } catch (e: any) {
            const msg =
                e?.response?.data?.error_description ||
                e?.response?.data?.error ||
                e?.message ||
                'Unknown error';
            Alert.alert('Login failed', msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Screen>
            <View style={styles.card}>
                <Text style={styles.title}>Login</Text>
                <Text style={styles.subtitle}>Sign in with your Keycloak account.</Text>

                <Text style={styles.label}>Username</Text>
                <TextInput
                    value={username}
                    onChangeText={setUsername}
                    autoCapitalize="none"
                    autoCorrect={false}
                    placeholder="username"
                    placeholderTextColor="rgba(255,255,255,0.35)"
                    style={styles.input}
                />

                <Text style={styles.label}>Password</Text>
                <TextInput
                    value={password}
                    onChangeText={setPassword}
                    secureTextEntry
                    autoCapitalize="none"
                    autoCorrect={false}
                    placeholder="password"
                    placeholderTextColor="rgba(255,255,255,0.35)"
                    style={styles.input}
                />

                <PrimaryButton
                    title={loading ? 'Signing in...' : 'Login'}
                    onPress={onLogin}
                    style={{ marginTop: 14 }}
                    disabled={loading}
                />

                <Text style={styles.link} onPress={() => void openForgotPassword()}>
                    Forgot password?
                </Text>

                <View style={styles.divider} />

                <Text style={styles.helper}>Don’t have an account?</Text>
                <PrimaryButton
                    title="Create account"
                    onPress={() => navigation.navigate('Register')}
                    style={{ marginTop: 10 }}
                />
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    card: {
        backgroundColor: 'rgba(15,23,42,0.96)',
        borderRadius: 24,
        padding: 20,
    },
    title: { color: '#FFFFFF', fontSize: 20, fontWeight: '800' },
    subtitle: { color: '#9CA3AF', marginTop: 6, lineHeight: 18 },

    label: {
        color: '#A7F3D0',
        fontSize: 11,
        fontWeight: '700',
        marginTop: 14,
        marginBottom: 6,
    },
    input: {
        height: 44,
        borderRadius: 12,
        paddingHorizontal: 12,
        color: '#FFFFFF',
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.10)',
    },

    link: {
        color: '#A7F3D0',
        marginTop: 12,
        fontSize: 12,
        fontWeight: '700',
    },

    divider: {
        height: 1,
        backgroundColor: 'rgba(255,255,255,0.10)',
        marginVertical: 16,
    },
    helper: { color: '#9CA3AF', fontSize: 12 },
});
