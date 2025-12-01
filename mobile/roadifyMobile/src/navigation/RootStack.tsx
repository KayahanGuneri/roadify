// src/navigation/RootStack.tsx

/**
 * RootStack.tsx
 *
 * English:
 * Defines the main stack navigator for the Roadify app.
 * This stack will hold all major screens such as Home, RouteSelection, etc.
 *
 * Türkçe Özet:
 * Roadify uygulamasındaki ana Stack Navigator yapısını tanımlar.
 * Home, RouteSelection gibi ana ekranlar bu stack içinde tutulur.
 */

import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from '../screens/HomeScreen';

export type RootStackParamList = {
    Home: undefined;
    RouteSelection: undefined;
    RoutePreview: undefined;
    PlacesList: undefined;
    TripPlanner: undefined;
    AIScreen: undefined;
    AnalyticsScreen: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export function RootStackNavigator() {
    return (
        <NavigationContainer>
            <Stack.Navigator
                initialRouteName="Home"
                screenOptions={{
                    headerShown: false,
                }}
            >
                <Stack.Screen name="Home" component={HomeScreen} />
                {/* Placeholder screens for future phases */}
                <Stack.Screen name="RouteSelection" component={HomeScreen} />
                <Stack.Screen name="RoutePreview" component={HomeScreen} />
                <Stack.Screen name="PlacesList" component={HomeScreen} />
                <Stack.Screen name="TripPlanner" component={HomeScreen} />
                <Stack.Screen name="AIScreen" component={HomeScreen} />
                <Stack.Screen name="AnalyticsScreen" component={HomeScreen} />
            </Stack.Navigator>
        </NavigationContainer>
    );
}
