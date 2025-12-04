// src/screens/SelectRouteScreen.tsx
import React from 'react';
import { View, Text, StyleSheet, TextInput } from 'react-native';
import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';

export const SelectRouteScreen: React.FC = ({ navigation }: any) => {
    const [fromCity, setFromCity] = React.useState('Antalya');
    const [toCity, setToCity] = React.useState('Istanbul');

    const onPreview = () => {
        // navigate to RoutePreview with coordinates from backend later
        navigation.navigate('RoutePreview', { fromCity, toCity });
    };

    return (
        <Screen>
            <Text style={styles.title}>Select Route</Text>
            <Text style={styles.subtitle}>
                Choose your start and destination – we’ll find the best road trip route.
            </Text>

            <View style={styles.card}>
                <Text style={styles.label}>From (city)</Text>
                <TextInput
                    value={fromCity}
                    onChangeText={setFromCity}
                    placeholder="Enter departure city"
                    placeholderTextColor="#6B7280"
                    style={styles.input}
                />
                <Text style={styles.label}>To (city)</Text>
                <TextInput
                    value={toCity}
                    onChangeText={setToCity}
                    placeholder="Enter destination city"
                    placeholderTextColor="#6B7280"
                    style={styles.input}
                />

                <Text style={styles.helper}>
                    Demo: try "Antalya", "Istanbul", "Ankara".
                </Text>

                <PrimaryButton title="Preview Route" onPress={onPreview} style={{ marginTop: 16 }} />
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    title: {
        color: '#FFFFFF',
        fontSize: 22,
        fontWeight: '700',
        marginBottom: 4,
    },
    subtitle: {
        color: '#D1D5DB',
        fontSize: 14,
        marginBottom: 16,
    },
    card: {
        backgroundColor: '#0F172A',
        borderRadius: 24,
        padding: 16,
    },
    label: {
        color: '#E5E7EB',
        marginTop: 8,
        marginBottom: 4,
        fontSize: 14,
    },
    input: {
        backgroundColor: '#020617',
        borderRadius: 12,
        paddingHorizontal: 12,
        paddingVertical: 10,
        color: '#FFFFFF',
        fontSize: 14,
    },
    helper: {
        color: '#9CA3AF',
        marginTop: 8,
        fontSize: 12,
    },
});
