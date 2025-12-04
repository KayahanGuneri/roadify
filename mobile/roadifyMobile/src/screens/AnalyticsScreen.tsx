import React from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';
import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';

const hero = require('../assets/illustrations/hero.png');

export const AnalyticsScreen: React.FC<any> = ({ navigation }) => {
    return (
        <Screen>
            <View style={styles.container}>
                <View style={styles.card}>
                    <Text style={styles.title}>Trip analytics</Text>
                    <Text style={styles.subtitle}>
                        Soon you&apos;ll see stats like total distance, favorite stops,
                        fuel cost estimates and more.
                    </Text>

                    <Image source={hero} style={styles.image} resizeMode="cover" />

                    <View style={styles.row}>
                        <View style={styles.statBox}>
                            <Text style={styles.statLabel}>Trips</Text>
                            <Text style={styles.statValue}>0</Text>
                        </View>
                        <View style={styles.statBox}>
                            <Text style={styles.statLabel}>Total km</Text>
                            <Text style={styles.statValue}>0</Text>
                        </View>
                        <View style={styles.statBox}>
                            <Text style={styles.statLabel}>AI picks</Text>
                            <Text style={styles.statValue}>0</Text>
                        </View>
                    </View>

                    <PrimaryButton
                        title="Back to Home"
                        onPress={() => navigation.navigate('Home')}
                        style={{ marginTop: 16 }}
                    />
                </View>
            </View>
        </Screen>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 16,
    },
    card: {
        flex: 1,
        backgroundColor: 'rgba(15,23,42,0.96)',
        borderRadius: 24,
        padding: 20,
    },
    title: {
        color: '#FFFFFF',
        fontSize: 22,
        fontWeight: '700',
        marginBottom: 4,
    },
    subtitle: {
        color: '#9CA3AF',
        fontSize: 14,
        marginBottom: 16,
    },
    image: {
        width: '100%',
        height: 170,
        borderRadius: 20,
        marginBottom: 16,
    },
    row: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        gap: 8,
    },
    statBox: {
        flex: 1,
        backgroundColor: '#020617',
        borderRadius: 16,
        paddingVertical: 12,
        paddingHorizontal: 10,
        alignItems: 'center',
    },
    statLabel: {
        color: '#9CA3AF',
        fontSize: 12,
    },
    statValue: {
        color: '#FFFFFF',
        fontSize: 18,
        fontWeight: '700',
        marginTop: 4,
    },
});
