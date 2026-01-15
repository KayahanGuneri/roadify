// src/screens/AIScreen.tsx
import React, { useCallback, useMemo, useState } from 'react';
import {
    ActivityIndicator,
    FlatList,
    KeyboardAvoidingView,
    Platform,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from 'react-native';
import { Screen } from '../components/Screen';
import { PrimaryButton } from '../components/PrimaryButton';
import { useTripContext } from '../context/TripContext';
import { useTrip } from '../hooks/useTrip';
import { useAiChat } from '../hooks/useAiChat';
import type { AISuggestionDTO, ChatMessage } from '../types/ai';
import type { UpdateTripStopsRequestDTO } from '../types/trips';
import { useAuth } from '../context/AuthContext'; // ← BURASI ÖNEMLİ

export const AIScreen: React.FC = () => {
    const { currentTripId } = useTripContext();
    const { accessToken } = useAuth(); // ← useAuth kullanıyoruz

    const { tripQuery, updateStopsMutation } = useTrip(accessToken, currentTripId);

    const routeId = tripQuery.data?.routeId ?? null;

    const { messages, isSending, error, sendMessage } = useAiChat({
        routeId,
        filters: null,
    });

    const [input, setInput] = useState('');

    const isTripLoading = tripQuery.isLoading;
    const activeTrip = tripQuery.data ?? null;

    const handleSend = useCallback(
        async () => {
            const text = input.trim();
            if (!text) return;
            setInput('');
            await sendMessage(text);
        },
        [input, sendMessage],
    );

    const handleAddSuggestionToTrip = useCallback(
        async (suggestion: AISuggestionDTO) => {
            if (!currentTripId || !activeTrip) {
                // eslint-disable-next-line no-console
                console.warn('[AIScreen] No active trip for add-to-trip');
                return;
            }

            const nextOrderIndex = (activeTrip.stops?.length ?? 0) + 1;

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
            } catch (e) {
                // eslint-disable-next-line no-console
                console.warn('[AIScreen][addSuggestionToTrip][FAIL]', e);
            }
        },
        [activeTrip, currentTripId, updateStopsMutation],
    );

    const renderMessage = useCallback(
        ({ item }: { item: ChatMessage }) => {
            const isUser = item.role === 'user';

            return (
                <View
                    style={[
                        styles.messageContainer,
                        isUser ? styles.messageUser : styles.messageAi,
                    ]}
                >
                    <Text
                        style={[
                            styles.messageText,
                            isUser ? styles.messageTextUser : styles.messageTextAi,
                        ]}
                    >
                        {item.text}
                    </Text>

                    {!isUser &&
                        item.suggestions &&
                        item.suggestions.length > 0 && (
                            <View style={styles.suggestionsContainer}>
                                {item.suggestions.map((s) => (
                                    <View
                                        key={`${item.id}-${s.placeId}`}
                                        style={styles.suggestionCard}
                                    >
                                        <Text style={styles.suggestionTitle}>{s.name}</Text>
                                        <Text style={styles.suggestionReason}>
                                            {s.shortReason}
                                        </Text>

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

    const listData = useMemo(
        () => [...messages].sort((a, b) => a.createdAt - b.createdAt),
        [messages],
    );

    const canChat = !!routeId && !!currentTripId;

    return (
        <Screen>
            <KeyboardAvoidingView
                style={styles.root}
                behavior={Platform.OS === 'ios' ? 'padding' : undefined}
            >
                <View style={styles.header}>
                    <Text style={styles.headerTitle}>AI trip assistant</Text>
                    {isTripLoading && (
                        <ActivityIndicator size="small" style={{ marginLeft: 8 }} />
                    )}
                </View>

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
                    <TouchableOpacity
                        onPress={handleSend}
                        disabled={!canChat || isSending || !input.trim()}
                        style={[
                            styles.sendButton,
                            (!canChat || isSending || !input.trim()) &&
                            styles.sendButtonDisabled,
                        ]}
                    >
                        {isSending ? (
                            <ActivityIndicator size="small" />
                        ) : (
                            <Text style={styles.sendButtonText}>Send</Text>
                        )}
                    </TouchableOpacity>
                </View>
            </KeyboardAvoidingView>
        </Screen>
    );
};

const styles = StyleSheet.create({
    root: {
        flex: 1,
    },
    header: {
        paddingHorizontal: 16,
        paddingTop: 16,
        paddingBottom: 8,
        flexDirection: 'row',
        alignItems: 'center',
    },
    headerTitle: {
        color: '#FFFFFF',
        fontSize: 20,
        fontWeight: '700',
    },
    messagesList: {
        paddingHorizontal: 16,
        paddingBottom: 16,
    },
    messageContainer: {
        maxWidth: '80%',
        paddingHorizontal: 12,
        paddingVertical: 8,
        borderRadius: 16,
        marginBottom: 8,
    },
    messageUser: {
        alignSelf: 'flex-end',
        backgroundColor: '#0EA5E9',
    },
    messageAi: {
        alignSelf: 'flex-start',
        backgroundColor: '#111827',
    },
    messageText: {
        fontSize: 14,
    },
    messageTextUser: {
        color: '#FFFFFF',
    },
    messageTextAi: {
        color: '#E5E7EB',
    },
    suggestionsContainer: {
        marginTop: 8,
        gap: 8,
    },
    suggestionCard: {
        backgroundColor: '#020617',
        borderRadius: 12,
        padding: 10,
        borderWidth: 1,
        borderColor: '#1F2937',
    },
    suggestionTitle: {
        color: '#FFFFFF',
        fontWeight: '600',
        marginBottom: 4,
    },
    suggestionReason: {
        color: '#9CA3AF',
        fontSize: 12,
        marginBottom: 8,
    },
    suggestionButton: {
        alignSelf: 'flex-start',
    },
    inputContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 12,
        borderTopWidth: 1,
        borderTopColor: '#1F2937',
        backgroundColor: '#020617',
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
        marginRight: 8,
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
    sendButtonDisabled: {
        opacity: 0.5,
    },
    sendButtonText: {
        color: '#FFFFFF',
        fontWeight: '600',
    },
    infoBox: {
        marginHorizontal: 16,
        marginBottom: 8,
        padding: 10,
        borderRadius: 12,
        backgroundColor: 'rgba(15,23,42,0.9)',
    },
    infoText: {
        color: '#9CA3AF',
        fontSize: 13,
    },
    errorBox: {
        marginHorizontal: 16,
        marginBottom: 8,
        padding: 10,
        borderRadius: 12,
        backgroundColor: '#7F1D1D',
    },
    errorText: {
        color: '#FEE2E2',
        fontSize: 13,
    },
});
