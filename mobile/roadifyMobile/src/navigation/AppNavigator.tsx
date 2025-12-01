// src/navigation/AppNavigator.tsx

/**
 * AppNavigator.tsx
 *
 * English:
 * Root stack navigator for the Roadify mobile app.
 *
 * Türkçe Özet:
 * Roadify mobil uygulamasının ana Stack Navigator'ı.
 * Home ve diğer ekranları burada tanımlarız.
 */

import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import HomeScreen from '../screens/HomeScreen';
import PlaceholderScreen from '../screens/PlaceholderScreen';

// All possible routes in the root stack
export type RootStackParamList = {
    Home: undefined;
    RouteSelection: undefined;
    RoutePreview: undefined;
    PlacesList: undefined;
    TripPlanner: undefined;
    AIScreen: undefined;
    AnalyticsScreen: undefined;
};

// IMPORTANT: only ONE generic argument here
const Stack = createNativeStackNavigator<RootStackParamList>();

export const AppNavigator: React.FC = () => {
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
                <Stack.Screen
                    name="RouteSelection"
                    component={PlaceholderScreen}
                />
                <Stack.Screen
                    name="RoutePreview"
                    component={PlaceholderScreen}
                />
                <Stack.Screen
                    name="PlacesList"
                    component={PlaceholderScreen}
                />
                <Stack.Screen
                    name="TripPlanner"
                    component={PlaceholderScreen}
                />
                <Stack.Screen
                    name="AIScreen"
                    component={PlaceholderScreen}
                />
                <Stack.Screen
                    name="AnalyticsScreen"
                    component={PlaceholderScreen}
                />
            </Stack.Navigator>
        </NavigationContainer>
    );
};
