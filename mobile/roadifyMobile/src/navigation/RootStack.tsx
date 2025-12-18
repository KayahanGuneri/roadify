// src/navigation/RootStack.tsx
import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import { HomeScreen } from '../screens/HomeScreen';
import { RouteSelectionScreen } from '../screens/RouteSelectionScreen';
import { RoutePreviewScreen } from '../screens/RoutePreviewScreen';
import { RouteMapFullScreen } from '../screens/RouteMapFullScreen';
import { PlacesListScreen } from '../screens/PlacesListScreen';
import { AIScreen } from '../screens/AIScreen';
import { AnalyticsScreen } from '../screens/AnalyticsScreen';

/**
 * RootStack.tsx
 *
 * English:
 * Root stack navigator for Roadify mobile app.
 * Defines navigation routes and typed params for each screen.
 *
 * Türkçe Özet:
 * Uygulamanın ana (root) navigation stack’i. Screen isimlerini ve parametre tiplerini burada tanımlarız.
 * M3 kapsamında PlacesList ekranı bu stack’e eklenir ve RoutePreview'dan routeId ile navigate edilir.
 */
export type RootStackParamList = {
    Home: undefined;
    RouteSelection: undefined;

    RoutePreview: {
        routeId: string;
        fromCity: string;
        toCity: string;
    };

    RouteMapFull: {
        routeId: string;
    };

    PlacesList: {
        routeId: string;
    };

    AIScreen: undefined;
    AnalyticsScreen: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export const RootStackNavigator: React.FC = () => {
    return (
        <Stack.Navigator
            initialRouteName="Home"
            screenOptions={{ headerShown: false }}
        >
            <Stack.Screen name="Home" component={HomeScreen} />
            <Stack.Screen name="RouteSelection" component={RouteSelectionScreen} />
            <Stack.Screen name="RoutePreview" component={RoutePreviewScreen} />
            <Stack.Screen name="RouteMapFull" component={RouteMapFullScreen} />

            {/* M3: Places list */}
            <Stack.Screen name="PlacesList" component={PlacesListScreen} />

            <Stack.Screen name="AIScreen" component={AIScreen} />
            <Stack.Screen name="AnalyticsScreen" component={AnalyticsScreen} />
        </Stack.Navigator>
    );
};

export default RootStackNavigator;
