import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RootStackNavigator } from './src/navigation/RootStack';
import { AuthProvider } from './src/context/AuthContext';
import { TripProvider } from './src/context/TripContext';

const queryClient = new QueryClient();

const App: React.FC = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>
                <TripProvider>
                    <NavigationContainer>
                        <RootStackNavigator />
                    </NavigationContainer>
                </TripProvider>
            </AuthProvider>
        </QueryClientProvider>
    );
};

export default App;
