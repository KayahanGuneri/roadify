import React, { useMemo, useState } from 'react';
import { View, Text, TextInput, StyleSheet, Image } from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PrimaryButton } from '../components/PrimaryButton';
import type { RootStackParamList } from '../navigation/types';
import { useRoutePreview } from '../hooks/useRoutePreview';
import type { RoutePreviewRequestDTO, RouteDTO } from '../types/routes';
import { getTextStyle, theme } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList, 'RouteSelection'>;

const CITY_COORDS: Record<string, { lat: number; lng: number }> = {
    antalya: { lat: 36.8841, lng: 30.7056 },
    istanbul: { lat: 41.0082, lng: 28.9784 },
    ankara: { lat: 39.9334, lng: 32.8597 },
};

function resolveCity(name: string) {
    const key = name.trim().toLowerCase();
    return CITY_COORDS[key] ?? null;
}

const hero = require('../assets/illustrations/hero.png');

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

    const combinedError = useMemo(() => {
        return localError || (error ? error.message : null);
    }, [localError, error]);

    const handlePreviewPress = () => {
        setLocalError(null);

        const from = resolveCity(fromCity);
        const to = resolveCity(toCity);

        if (!from || !to) {
            setLocalError('Demo şu an sadece: Antalya, Istanbul, Ankara şehirlerini destekliyor.');
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

    return (
        <Screen background="living">
            <AppBar title="Select Route" onBack={() => navigation.goBack()} />

            <View style={styles.header}>
                <Text style={styles.subtitle}>
                    Choose your origin and destination to preview your road trip.
                </Text>

                <Image source={hero} style={styles.hero} resizeMode="contain" />
            </View>

            <View style={styles.card}>
                <Text style={styles.sectionTitle}>Route details</Text>

                <Text style={styles.label}>From (city)</Text>
                <TextInput
                    style={styles.input}
                    value={fromCity}
                    onChangeText={setFromCity}
                    autoCapitalize="words"
                    placeholder="Antalya"
                    placeholderTextColor="rgba(255,255,255,0.35)"
                />

                <Text style={[styles.label, { marginTop: theme.spacing.md }]}>To (city)</Text>
                <TextInput
                    style={styles.input}
                    value={toCity}
                    onChangeText={setToCity}
                    autoCapitalize="words"
                    placeholder="Istanbul"
                    placeholderTextColor="rgba(255,255,255,0.35)"
                />

                <Text style={styles.helper}>
                    Demo cities: <Text style={styles.helperStrong}>Antalya, Istanbul, Ankara</Text>
                </Text>

                {combinedError ? <Text style={styles.error}>{combinedError}</Text> : null}

                <PrimaryButton
                    title={isPending ? 'Calculating…' : 'Preview Route'}
                    onPress={handlePreviewPress}
                    disabled={isPending}
                    style={{ marginTop: theme.spacing.md }}
                />
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    header: {
        marginTop: theme.spacing.sm,
        marginBottom: theme.spacing.md,
    },
    subtitle: {
        color: theme.colors.textMuted,
        ...getTextStyle('body'),
    },
    hero: {
        marginTop: theme.spacing.md,
        width: '100%',
        height: 140,
        borderRadius: theme.radius['2xl'],
        opacity: 0.95,
    },

    card: {
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderRadius: theme.radius['2xl'],
        padding: theme.spacing.lg,
        borderWidth: 1,
        borderColor: theme.colors.border,
    },
    sectionTitle: {
        color: theme.colors.text,
        ...getTextStyle('h2'),
        marginBottom: theme.spacing.sm,
    },
    label: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
        marginBottom: theme.spacing.xs,
    },
    input: {
        height: 44,
        borderRadius: theme.radius.md,
        paddingHorizontal: theme.spacing.md,
        color: theme.colors.text,
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderWidth: 1,
        borderColor: theme.colors.border,
    },
    helper: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
        marginTop: theme.spacing.sm,
    },
    helperStrong: {
        color: theme.colors.primary,
        fontWeight: '700',
    },
    error: {
        color: theme.colors.danger,
        ...getTextStyle('body'),
        marginTop: theme.spacing.sm,
    },
});

export default RouteSelectionScreen;
