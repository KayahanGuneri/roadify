import React from 'react';
import { useAuth } from '../context/AuthContext';
import RootStackNavigator from './RootStack';
import { AuthStack } from './AuthStack';

/**
 * RootNavigator
 *
 * English:
 * Switches between AuthStack and AppStack based on auth state.
 *
 * Türkçe Özet:
 * accessToken varsa AppStack, yoksa AuthStack gösterir.
 */
export const RootNavigator: React.FC = () => {
    const { isLoggedIn } = useAuth();
    return isLoggedIn ? <RootStackNavigator /> : <AuthStack />;
};
