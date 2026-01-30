import React, { useCallback, useMemo, useState } from 'react';
import {
    ActivityIndicator,
    FlatList,
    KeyboardAvoidingView,
    Platform,
    StyleSheet,
    Text,
    TextInput,
    View,
} from 'react-native';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PressableScale } from '../components/PressableScale';
import { PrimaryButton } from '../components/PrimaryButton';
import { useTripContext } from '../context/TripContext';
import { useTrip } from '../hooks/useTrip';
import { useAiChat } from '../hooks/useAiChat';
import type { AISuggestionDTO, ChatMessage } from '../types/ai';
import type { UpdateTripStopsRequestDTO } from '../types/trips';
import { useAuth } from '../context/AuthContext';
import { getTextStyle, theme } from '../theme/theme';

export const AIScreen: React.FC = () => {
    const { currentTripId } = useTripContext();
    const { accessToken } = useAuth();

    const { tripQuery, updateStopsMutation } = useTrip(accessToken, currentTripId);
    const routeId = tripQuery.data?.routeId ?? null;

    const { messages, isSending, error, sendMessage } = useAiChat({
        routeId,
        filters: null,
    });

    const [input, setInput] = useState('');

    const isTripLoading = tripQuery.isLoading;
    const canChat = !!routeId && !!currentTripId;

    const handleSend = useCallback(async () => {
        const text = input.trim();
        if (!text) return;
        setInput('');
        await sendMessage(text);
    }, [input, sendMessage]);

    const handleAddSuggestionToTrip = useCallback(
        async (suggestion: AISuggestionDTO) => {
            const activeTrip = tripQuery.data ?? null;
            if (!currentTripId || !activeTrip) return;

            const nextOrderIndex = activeTrip.stops?.length ?? 0;

            const req: UpdateTripStopsRequestDTO = {
                add: [
                    {
                        placeId: suggestion.placeId,
                        placeName: suggestion.name,
                        orderIndex: nextOrderIndex,
                        plannedArrivalTime: null,
                        plannedDurationMinutes: null,
                    },
                ],
                removeIds: [],
            };

            try {
                await updateStopsMutation.mutateAsync({
                    tripId: currentTripId,
                    req,
                });
            } catch {
                // ignore for UI phase
            }
        },
        [currentTripId, tripQuery.data, updateStopsMutation],
    );

    const renderMessage = useCallback(
        ({ item }: { item: ChatMessage }) => {
            const isUser = item.role === 'user';

            return (
                <View style={[styles.messageContainer, isUser ? styles.messageUser : styles.messageAi]}>
                    <Text style={[styles.messageText, isUser ? styles.messageTextUser : styles.messageTextAi]}>
                        {item.text}
                    </Text>

                    {!isUser && item.suggestions && item.suggestions.length > 0 && (
                        <View style={styles.suggestionsContainer}>
                            {item.suggestions.map((s) => (
                                <View key={`${item.id}-${s.placeId}`} style={styles.suggestionCard}>
                                    <Text style={styles.suggestionTitle}>{s.name}</Text>
                                    <Text style={styles.suggestionReason}>{s.shortReason}</Text>

                                    <PrimaryButton
                                        title="Add to trip"
                                        onPress={() => handleAddSuggestionToTrip(s)}
                                        style={styles.suggestionButton}
                                    />
                                </View>
                            ))}
                        </View>
                    )}
                </View>
            );
        },
        [handleAddSuggestionToTrip],
    );

    const listData = useMemo(() => [...messages].sort((a, b) => a.createdAt - b.createdAt), [messages]);

    return (
        <Screen>
            <AppBar title="AI assistant" />

            <KeyboardAvoidingView style={styles.root} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
                {isTripLoading && (
                    <View style={styles.loadingInline}>
                        <ActivityIndicator size="small" color={theme.colors.primary} />
                        <Text style={styles.loadingInlineText}>Loading trip…</Text>
                    </View>
                )}

                {!canChat && (
                    <View style={styles.infoBox}>
                        <Text style={styles.infoText}>
                            To use AI suggestions, please select or create a trip first.
                        </Text>
                    </View>
                )}

                {error && (
                    <View style={styles.errorBox}>
                        <Text style={styles.errorText}>
                            AI is temporarily unavailable. Please try again.
                        </Text>
                    </View>
                )}

                <FlatList
                    data={listData}
                    keyExtractor={(item) => item.id}
                    renderItem={renderMessage}
                    contentContainerStyle={styles.messagesList}
                    keyboardShouldPersistTaps="handled"
                    showsVerticalScrollIndicator={false}
                />

                <View style={styles.inputContainer}>
                    <TextInput
                        value={input}
                        onChangeText={setInput}
                        placeholder="Ask Roadify about your route..."
                        placeholderTextColor="#9CA3AF"
                        style={styles.input}
                        editable={canChat && !isSending}
                    />

                    <PressableScale
                        onPress={handleSend}
                        disabled={!canChat || isSending || !input.trim()}
                        contentStyle={styles.sendButton}
                    >
                        {isSending ? (
                            <ActivityIndicator size="small" />
                        ) : (
                            <Text style={styles.sendButtonText}>Send</Text>
                        )}
                    </PressableScale>
                </View>
            </KeyboardAvoidingView>
        </Screen>
    );
};

const styles = StyleSheet.create({
    root: { flex: 1 },

    loadingInline: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: theme.spacing.sm,
        paddingHorizontal: theme.spacing.lg,
        paddingTop: theme.spacing.md,
    },
    loadingInlineText: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
    },

    messagesList: {
        paddingHorizontal: theme.spacing.lg,
        paddingTop: theme.spacing.md,
        paddingBottom: theme.spacing.md,
    },

    messageContainer: {
        maxWidth: '80%',
        paddingHorizontal: 12,
        paddingVertical: 8,
        borderRadius: 16,
        marginBottom: 8,
    },
    messageUser: { alignSelf: 'flex-end', backgroundColor: '#0EA5E9' },
    messageAi: { alignSelf: 'flex-start', backgroundColor: '#111827' },

    messageText: { fontSize: 14 },
    messageTextUser: { color: '#FFFFFF' },
    messageTextAi: { color: '#E5E7EB' },

    suggestionsContainer: { marginTop: 8, gap: 8 },

    suggestionCard: {
        backgroundColor: '#020617',
        borderRadius: 12,
        padding: 10,
        borderWidth: 1,
        borderColor: '#1F2937',
    },
    suggestionTitle: { color: '#FFFFFF', fontWeight: '600', marginBottom: 4 },
    suggestionReason: { color: '#9CA3AF', fontSize: 12, marginBottom: 8 },
    suggestionButton: { alignSelf: 'flex-start' },

    inputContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: theme.spacing.md,
        borderTopWidth: 1,
        borderTopColor: '#1F2937',
        backgroundColor: '#020617',
        gap: theme.spacing.sm,
    },
    input: {
        flex: 1,
        paddingHorizontal: 12,
        paddingVertical: 8,
        backgroundColor: '#020617',
        borderRadius: 999,
        borderWidth: 1,
        borderColor: '#1F2937',
        color: '#FFFFFF',
        fontSize: 14,
    },

    sendButton: {
        paddingHorizontal: 16,
        paddingVertical: 8,
        borderRadius: 999,
        backgroundColor: '#0EA5E9',
        justifyContent: 'center',
        alignItems: 'center',
    },
    sendButtonText: { color: '#FFFFFF', fontWeight: '600' },

    infoBox: {
        marginHorizontal: theme.spacing.lg,
        marginTop: theme.spacing.md,
        marginBottom: theme.spacing.sm,
        padding: 10,
        borderRadius: 12,
        backgroundColor: 'rgba(15,23,42,0.9)',
    },
    infoText: { color: '#9CA3AF', fontSize: 13 },

    errorBox: {
        marginHorizontal: theme.spacing.lg,
        marginBottom: theme.spacing.sm,
        padding: 10,
        borderRadius: 12,
        backgroundColor: '#7F1D1D',
    },
    errorText: { color: '#FEE2E2', fontSize: 13 },
});
