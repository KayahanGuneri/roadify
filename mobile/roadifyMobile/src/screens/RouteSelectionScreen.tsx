// src/screens/RouteSelectionScreen.tsx
import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, Image } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { RootStackParamList } from '../navigation/RootStack';
import { useRoutePreview } from '../hooks/useRoutePreview';
import type { RoutePreviewRequestDTO, RouteDTO } from '../types/routes';

type Props = NativeStackScreenProps<RootStackParamList, 'RouteSelection'>;

// Demo city -> coordinate map (şu an için 3 şehir)
const CITY_COORDS: Record<string, { lat: number; lng: number }> = {
    antalya: { lat: 36.8841, lng: 30.7056 },
    istanbul: { lat: 41.0082, lng: 28.9784 },
    ankara: { lat: 39.9334, lng: 32.8597 },
};

function resolveCity(name: string) {
    const key = name.trim().toLowerCase();
    return CITY_COORDS[key] ?? null;
}

export const RouteSelectionScreen: React.FC<Props> = ({ navigation }) => {
    const [fromCity, setFromCity] = useState('Antalya');
    const [toCity, setToCity] = useState('Istanbul');
    const [localError, setLocalError] = useState<string | null>(null);

    const { mutate, isPending, error } = useRoutePreview({
        onSuccess: (route: RouteDTO) => {

            navigation.navigate('RoutePreview', {
                routeId: route.id,
                fromCity,
                toCity,
            });
        },
    });

    const handlePreviewPress = () => {
        setLocalError(null);

        const from = resolveCity(fromCity);
        const to = resolveCity(toCity);

        if (!from || !to) {
            setLocalError(
                'Demo şu an sadece: Antalya, Istanbul, Ankara şehirlerini destekliyor.'
            );
            return;
        }

        const body: RoutePreviewRequestDTO = {
            fromLat: from.lat,
            fromLng: from.lng,
            toLat: to.lat,
            toLng: to.lng,
        };

        mutate(body);
    };

    const combinedError = localError || (error ? error.message : null);

    return (
        <Screen>
            {/* Üstte küçük hero görseli + açıklama */}
            <View style={styles.header}>
                <Text style={styles.title}>Select Route</Text>
                <Text style={styles.subtitle}>
                    Choose your origin and destination to preview your road trip.
                </Text>

                <Image
                    source={require('../assets/illustrations/hero.png')}
                    style={styles.hero}
                    resizeMode="contain"
                />
            </View>

            {/* Kart içinde form */}
            <View style={styles.card}>
                <Text style={styles.label}>From (city)</Text>
                <TextInput
                    style={styles.input}
                    value={fromCity}
                    onChangeText={setFromCity}
                    autoCapitalize="words"
                />

                <Text style={styles.label}>To (city)</Text>
                <TextInput
                    style={styles.input}
                    value={toCity}
                    onChangeText={setToCity}
                    autoCapitalize="words"
                />

                <Text style={styles.helper}>
                    Demo cities:{' '}
                    <Text style={styles.helperStrong}>Antalya, Istanbul, Ankara</Text>
                </Text>

                {combinedError && <Text style={styles.error}>{combinedError}</Text>}

                <PrimaryButton
                    title={isPending ? 'Calculating…' : 'Preview Route'}
                    onPress={handlePreviewPress}
                    style={{ marginTop: 16 }}
                />
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    header: {
        marginBottom: 16,
    },
    title: {
        color: '#FFFFFF',
        fontSize: 22,
        fontWeight: '700',
    },
    subtitle: {
        color: '#9CA3AF',
        marginTop: 4,
    },
    hero: {
        marginTop: 16,
        width: '100%',
        height: 140,
        borderRadius: 24,
    },
    card: {
        backgroundColor: 'rgba(15,23,42,0.96)',
        borderRadius: 24,
        padding: 18,
    },
    label: {
        color: '#E5E7EB',
        marginTop: 8,
        marginBottom: 4,
    },
    input: {
        borderWidth: 1,
        borderColor: '#1F2937',
        backgroundColor: '#020617',
        color: '#F9FAFB',
        borderRadius: 10,
        paddingHorizontal: 10,
        paddingVertical: 8,
    },
    helper: {
        color: '#9CA3AF',
        marginTop: 8,
    },
    helperStrong: {
        color: '#6EE7B7',
        fontWeight: '600',
    },
    error: {
        color: '#F97373',
        marginTop: 8,
    },
});
