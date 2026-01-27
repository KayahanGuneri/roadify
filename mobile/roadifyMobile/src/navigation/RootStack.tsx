// src/navigation/RootStack.tsx
import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import { HomeScreen } from '../screens/HomeScreen';
import { RouteSelectionScreen } from '../screens/RouteSelectionScreen';
import { RoutePreviewScreen } from '../screens/RoutePreviewScreen';
import { RouteMapFullScreen } from '../screens/RouteMapFullScreen';
import { PlacesListScreen } from '../screens/PlacesListScreen';
import { TripPlannerScreen } from '../screens/TripPlannerScreen';
import { AIScreen } from '../screens/AIScreen';
import { AnalyticsScreen } from '../screens/AnalyticsScreen';
import type { RootStackParamList } from './types';

/**
 * RootStack.tsx
 *
 * English:
 * App (authenticated) navigation stack.
 *
 * Türkçe Özet:
 * Login sonrası kullanılan ana navigation stack.
 */
const Stack = createNativeStackNavigator<RootStackParamList>();

export const RootStackNavigator: React.FC = () => {
    return (
        <Stack.Navigator initialRouteName="Home" screenOptions={{ headerShown: false }}>
            <Stack.Screen name="Home" component={HomeScreen} />
            <Stack.Screen name="RouteSelection" component={RouteSelectionScreen} />
            <Stack.Screen name="RoutePreview" component={RoutePreviewScreen} />
            <Stack.Screen name="RouteMapFull" component={RouteMapFullScreen} />
            <Stack.Screen name="PlacesList" component={PlacesListScreen} />
            <Stack.Screen name="TripPlanner" component={TripPlannerScreen} />
            <Stack.Screen name="AIScreen" component={AIScreen} />
            <Stack.Screen name="AnalyticsScreen" component={AnalyticsScreen} />
        </Stack.Navigator>
    );
};

export default RootStackNavigator;
