// App.tsx
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RootStackNavigator } from './src/navigation/RootStack';

const queryClient = new QueryClient();

const App: React.FC = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <NavigationContainer>
                <RootStackNavigator />
            </NavigationContainer>
        </QueryClientProvider>
    );
};

export default App;
