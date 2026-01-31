import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../../components/Screen';
import { AppBar } from '../../components/AppBar';
import { PrimaryButton } from '../../components/PrimaryButton';
import type { RootStackParamList } from '../../navigation/types';
import { openRegister } from '../../lib/keycloakLinks';
import { theme, getTextStyle } from '../../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'Register'>;

export const RegisterScreen: React.FC<Props> = ({ navigation }) => {
    return (
        <Screen background="living">
            <AppBar title="Create account" onBack={() => navigation.goBack()} />

            <View style={styles.card}>
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
                    style={{ marginTop: theme.spacing.md }}
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
        marginTop: theme.spacing.md,
        backgroundColor: 'rgba(15,23,42,0.92)',
        borderRadius: theme.radius['2xl'],
        padding: theme.spacing.lg,
        borderWidth: 1,
        borderColor: theme.colors.border,
        ...theme.elevation.e1,
    },

    subtitle: { color: theme.colors.textMuted, marginTop: 2, lineHeight: 18 },

    callout: {
        marginTop: theme.spacing.md,
        padding: 14,
        borderRadius: theme.radius.lg,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.25)',
        backgroundColor: theme.colors.primarySoft,
    },
    calloutTitle: { color: theme.colors.primary, ...getTextStyle('bodyMedium'), marginBottom: 6 },
    calloutText: { color: 'rgba(255,255,255,0.82)', fontSize: 13, lineHeight: 18 },
});
