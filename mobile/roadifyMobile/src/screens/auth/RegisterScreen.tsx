import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../../components/Screen';
import { PrimaryButton } from '../../components/PrimaryButton';
import type { RootStackParamList } from '../../navigation/types';
import { openRegister } from '../../lib/keycloakLinks';

type Props = NativeStackScreenProps<RootStackParamList, 'Register'>;

export const RegisterScreen: React.FC<Props> = ({ navigation }) => {
    return (
        <Screen>
            <View style={styles.card}>
                <Text style={styles.title}>Create account</Text>
                <Text style={styles.subtitle}>
                    Registration is handled securely by Keycloak. We’ll open the official registration page.
                </Text>

                <View style={styles.callout}>
                    <Text style={styles.calloutTitle}>Tip</Text>
                    <Text style={styles.calloutText}>
                        After completing registration, you’ll be redirected back to the app automatically.
                    </Text>
                </View>

                <PrimaryButton
                    title="Open Keycloak registration"
                    onPress={() => void openRegister()}
                    style={{ marginTop: 14 }}
                />

                <PrimaryButton
                    title="Back to Login"
                    onPress={() => navigation.navigate('Login')}
                    style={{ marginTop: 12 }}
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
    subtitle: { color: '#9CA3AF', marginTop: 8, lineHeight: 18 },

    callout: {
        marginTop: 16,
        padding: 14,
        borderRadius: 16,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.25)',
        backgroundColor: 'rgba(52, 211, 153, 0.08)',
    },
    calloutTitle: { color: '#A7F3D0', fontWeight: '800', marginBottom: 6 },
    calloutText: { color: 'rgba(255,255,255,0.82)', fontSize: 13, lineHeight: 18 },
});
