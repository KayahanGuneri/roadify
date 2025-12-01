// App.tsx

/**
 * App.tsx
 *
 * English:
 * Entry point of the Roadify mobile app. Wraps the root stack navigator.
 *
 * Türkçe Özet:
 * Roadify mobil uygulamasının giriş noktası. Root stack navigator'ı burada çağrılır.
 */

import React from 'react';
import { RootStackNavigator } from './src/navigation/RootStack';

export default function App() {
    return <RootStackNavigator />;
}
