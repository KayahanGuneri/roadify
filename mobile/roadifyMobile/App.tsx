import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './src/context/AuthContext';
import { TripProvider } from './src/context/TripContext';
import { RootNavigator } from './src/navigation/RootNavigator';

const queryClient = new QueryClient();

const App: React.FC = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>
                <TripProvider>
                    <NavigationContainer>
                        <RootNavigator />
                    </NavigationContainer>
                </TripProvider>
            </AuthProvider>
        </QueryClientProvider>
    );
};

export default App;
