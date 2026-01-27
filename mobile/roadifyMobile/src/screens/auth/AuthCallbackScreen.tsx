import React, { useEffect } from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../navigation/types';

type Props = NativeStackScreenProps<RootStackParamList, 'AuthCallback'>;

/**
 * AuthCallbackScreen
 *
 * English:
 * Handles deep link redirect from Keycloak after register/reset flows.
 *
 * Türkçe Özet:
 * Keycloak işlemleri bitince deep link ile app buraya döner.
 * U1 scope: login ekranına yönlendirir.
 */
export const AuthCallbackScreen: React.FC<Props> = ({ navigation }) => {
    useEffect(() => {
        navigation.reset({ index: 0, routes: [{ name: 'Login' }] });
    }, [navigation]);

    return (
        <View style={styles.container}>
            <ActivityIndicator />
            <Text style={styles.text}>Returning to app…</Text>
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, alignItems: 'center', justifyContent: 'center' },
    text: { marginTop: 10, fontSize: 12, opacity: 0.7 },
});
