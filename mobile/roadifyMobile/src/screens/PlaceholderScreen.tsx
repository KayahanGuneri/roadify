// src/screens/PlaceholderScreen.tsx

/**
 * PlaceholderScreen.tsx
 *
 * English:
 * Temporary placeholder screen for routes that are not yet implemented.
 *
 * Türkçe Özet:
 * Henüz implement edilmemiş ekranlar için geçici placeholder.
 */

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import {spacing,colors} from "../theme/theme.ts";

const PlaceholderScreen: React.FC = () => {
    return (
        <View style={styles.container}>
            <Text style={styles.text}>Coming soon...</Text>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: colors.background,
        alignItems: 'center',
        justifyContent: 'center',
    },
    text: {
        color: colors.textPrimary,
        fontSize: 18,
    },
});

export default PlaceholderScreen;
