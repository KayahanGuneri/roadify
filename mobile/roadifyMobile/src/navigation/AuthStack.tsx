import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import type { RootStackParamList } from './types';

import { LoginScreen } from '../screens/auth/LoginScreen';
import { RegisterScreen } from '../screens/auth/RegisterScreen';
import { AuthCallbackScreen } from '../screens/auth/AuthCallbackScreen';

const Stack = createNativeStackNavigator<RootStackParamList>();

export const AuthStack: React.FC = () => {
    return (
        <Stack.Navigator initialRouteName="Login" screenOptions={{ headerShown: false }}>
            <Stack.Screen name="Login" component={LoginScreen} />
            <Stack.Screen name="Register" component={RegisterScreen} />
            <Stack.Screen name="AuthCallback" component={AuthCallbackScreen} />
        </Stack.Navigator>
    );
};
