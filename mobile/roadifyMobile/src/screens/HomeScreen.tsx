// src/screens/HomeScreen.tsx

/**
 * HomeScreen.tsx
 *
 * English:
 * Landing screen for Roadify. Shows the main tagline and entry points
 * to plan trips, see existing trips, open the AI assistant and view analytics.
 *
 * Türkçe Özet:
 * Roadify uygulamasının açılış ekranı. Ana sloganı ve
 * rota planlama, kayıtlı seyahatler, yapay zeka asistanı ve
 * analiz ekranlarına giriş butonlarını içerir.
 */

import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/RootStack';
import { colors, spacing, typography } from '../theme/theme';

type Props = NativeStackScreenProps<RootStackParamList>;


const HomeScreen: React.FC<Props> = ({ navigation }) => {
    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={styles.appName}>Roadify</Text>
                <Text style={styles.subtitle}>Plan Smarter Road Trips</Text>
            </View>

            <View style={styles.buttonsContainer}>
                <PrimaryButton
                    label="Plan Trip"
                    onPress={() => navigation.navigate('RouteSelection')}
                />
                <PrimaryButton
                    label="My Trips"
                    onPress={() => navigation.navigate('TripPlanner')}
                />
                <PrimaryButton
                    label="AI Assistant"
                    onPress={() => navigation.navigate('AIScreen')}
                />
                <PrimaryButton
                    label="Analytics"
                    onPress={() => navigation.navigate('AnalyticsScreen')}
                />
            </View>
        </View>
    );
};

type PrimaryButtonProps = {
    label: string;
    onPress: () => void;
};

const PrimaryButton: React.FC<PrimaryButtonProps> = ({ label, onPress }) => {
    return (
        <TouchableOpacity style={styles.primaryButton} onPress={onPress}>
            <Text style={styles.primaryButtonText}>{label}</Text>
        </TouchableOpacity>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: colors.background,
        paddingHorizontal: spacing.lg,
        paddingVertical: spacing.xl,
        justifyContent: 'space-between',
    },
    header: {
        marginTop: spacing.xl,
    },
    appName: {
        color: colors.primary,
        fontSize: typography.title,
        fontWeight: 'bold',
    },
    subtitle: {
        marginTop: spacing.sm,
        color: colors.textSecondary,
        fontSize: typography.subtitle,
    },
    buttonsContainer: {
        marginBottom: spacing.xl,
    },
    primaryButton: {
        backgroundColor: colors.primary,
        paddingVertical: spacing.md,
        paddingHorizontal: spacing.lg,
        borderRadius: 12,
        marginBottom: spacing.md,
    },
    primaryButtonText: {
        color: colors.textPrimary,
        fontSize: typography.body,
        fontWeight: '600',
        textAlign: 'center',
    },
});

export default HomeScreen;
