// src/screens/AIScreen.tsx
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
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
import { useNavigation } from '@react-navigation/native';

import { Screen } from '../components/Screen';
import { AppBar } from '../components/AppBar';
import { PressableScale } from '../components/PressableScale';
import { PrimaryButton } from '../components/PrimaryButton';

import { useTripContext } from '../context/TripContext';
import { useAuth } from '../context/AuthContext';

import { useTrip } from '../hooks/useTrip';
import { useAiChat } from '../hooks/useAiChat';

import type { AISuggestionDTO, ChatMessage } from '../types/ai';
import type { UpdateTripStopsRequestDTO } from '../types/trips';
import { getTextStyle, theme } from '../theme/theme';

export const AIScreen: React.FC = () => {
    const navigation = useNavigation<any>();

    const { currentTripId } = useTripContext();
    const { accessToken } = useAuth();

    const { tripQuery, updateStopsMutation } = useTrip(accessToken, currentTripId);

    // AI endpoint requires routeId; routeId exists only when we have an active trip loaded.
    const routeId = tripQuery.data?.routeId ?? null;

    const { messages, isSending, error, sendMessage } = useAiChat({
        routeId,
        filters: null,
    });

    const [input, setInput] = useState('');

    const canChat = Boolean(currentTripId && routeId);
    const isTripLoading = tripQuery.isLoading;

    const listRef = useRef<FlatList<ChatMessage>>(null);

    const listData = useMemo(
        () => [...messages].sort((a, b) => a.createdAt - b.createdAt),
        [messages],
    );

    // Auto-scroll on new messages
    useEffect(() => {
        if (!listData.length) return;
        const t = setTimeout(() => {
            listRef.current?.scrollToEnd({ animated: true });
        }, 60);
        return () => clearTimeout(t);
    }, [listData.length]);

    const handleSend = useCallback(async () => {
        const text = input.trim();
        if (!text) return;
        setInput('');
        await sendMessage(text);
    }, [input, sendMessage]);

    const handleAddSuggestionToTrip = useCallback(
        async (s: AISuggestionDTO) => {
            const activeTrip = tripQuery.data ?? null;
            if (!currentTripId || !activeTrip) return;

            const nextOrderIndex = activeTrip.stops?.length ?? 0;

            const req: UpdateTripStopsRequestDTO = {
                add: [
                    {
                        placeId: s.placeId,
                        placeName: s.name?.trim() ? s.name : null,
                        orderIndex: nextOrderIndex,
                        plannedArrivalTime: null,
                        plannedDurationMinutes: null,
                    },
                ],
                removeIds: [],
            };

            try {
                await updateStopsMutation.mutateAsync({ tripId: currentTripId, req });
            } catch {
                // UI-only phase: ignore (istersen burada toast/alert ekleriz)
            }
        },
        [currentTripId, tripQuery.data, updateStopsMutation],
    );

    const goToTrip = () => {
        if (!currentTripId) return;
        navigation.navigate('TripPlanner', { tripId: currentTripId });
    };

    const renderMessage = useCallback(
        ({ item }: { item: ChatMessage }) => {
            const isUser = item.role === 'user';

            return (
                <View style={[styles.row, isUser ? styles.rowUser : styles.rowAi]}>
                    <View style={[styles.bubble, isUser ? styles.bubbleUser : styles.bubbleAi]}>
                        <Text style={[styles.msgText, isUser ? styles.msgTextUser : styles.msgTextAi]}>
                            {item.text}
                        </Text>

                        {!isUser && item.suggestions && item.suggestions.length > 0 ? (
                            <View style={styles.suggestions}>
                                {item.suggestions.map((s) => (
                                    <View key={`${item.id}-${s.placeId}`} style={styles.suggestionCard}>
                                        <Text style={styles.suggestionTitle} numberOfLines={1}>
                                            {s.name}
                                        </Text>
                                        <Text style={styles.suggestionReason} numberOfLines={3}>
                                            {s.shortReason}
                                        </Text>

                                        <PrimaryButton
                                            title="Add to trip"
                                            onPress={() => handleAddSuggestionToTrip(s)}
                                            disabled={updateStopsMutation.isPending}
                                            style={styles.suggestionButton}
                                        />
                                    </View>
                                ))}
                            </View>
                        ) : null}
                    </View>
                </View>
            );
        },
        [handleAddSuggestionToTrip, updateStopsMutation.isPending],
    );

    const footer = useMemo(() => {
        if (!isSending) return null;
        return (
            <View style={styles.typingRow}>
                <ActivityIndicator size="small" color={theme.colors.primary} />
                <Text style={styles.typingText}>Thinking…</Text>
            </View>
        );
    }, [isSending]);

    return (
        <Screen background="living" noPadding>
            <AppBar
                title="AI assistant"
                right={{ label: 'Trip', onPress: goToTrip, disabled: !currentTripId }}
            />

            <KeyboardAvoidingView
                style={styles.root}
                behavior={Platform.OS === 'ios' ? 'padding' : undefined}
                keyboardVerticalOffset={Platform.OS === 'ios' ? 10 : 0}
            >
                {isTripLoading ? (
                    <View style={styles.stateInline}>
                        <ActivityIndicator size="small" color={theme.colors.primary} />
                        <Text style={styles.stateInlineText}>Loading trip…</Text>
                    </View>
                ) : null}

                {!canChat ? (
                    <View style={styles.noticeBox}>
                        <Text style={styles.noticeText}>
                            To use AI suggestions, please select or create a trip first.
                        </Text>
                    </View>
                ) : null}

                {error ? (
                    <View style={styles.errorBox}>
                        <Text style={styles.errorText}>AI is temporarily unavailable. Please try again.</Text>
                    </View>
                ) : null}

                <FlatList
                    ref={listRef}
                    data={listData}
                    keyExtractor={(m) => m.id}
                    renderItem={renderMessage}
                    ListFooterComponent={footer}
                    contentContainerStyle={styles.listContent}
                    keyboardShouldPersistTaps="handled"
                    showsVerticalScrollIndicator={false}
                    onContentSizeChange={() => {
                        if (listData.length > 0) listRef.current?.scrollToEnd({ animated: true });
                    }}
                />

                <View style={styles.inputBar}>
                    <TextInput
                        value={input}
                        onChangeText={setInput}
                        placeholder="Ask Roadify about your route..."
                        placeholderTextColor={theme.colors.textMuted}
                        style={styles.input}
                        editable={canChat && !isSending}
                        multiline
                        maxLength={600}
                    />

                    <PressableScale
                        onPress={handleSend}
                        disabled={!canChat || isSending || !input.trim()}
                        contentStyle={styles.sendBtn}
                        hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}
                    >
                        {isSending ? (
                            <ActivityIndicator size="small" color={theme.colors.primary} />
                        ) : (
                            <Text style={styles.sendText}>Send</Text>
                        )}
                    </PressableScale>
                </View>
            </KeyboardAvoidingView>
        </Screen>
    );
};

const styles = StyleSheet.create({
    root: { flex: 1 },

    stateInline: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: theme.spacing.sm,
        paddingHorizontal: theme.spacing.lg,
        paddingTop: theme.spacing.md,
    },
    stateInlineText: { color: theme.colors.textMuted, ...getTextStyle('caption') },

    noticeBox: {
        marginHorizontal: theme.spacing.lg,
        marginTop: theme.spacing.md,
        marginBottom: theme.spacing.sm,
        padding: theme.spacing.md,
        borderRadius: theme.radius.lg,
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderWidth: 1,
        borderColor: theme.colors.border,
    },
    noticeText: { color: theme.colors.textMuted, ...getTextStyle('body') },

    errorBox: {
        marginHorizontal: theme.spacing.lg,
        marginBottom: theme.spacing.sm,
        padding: theme.spacing.md,
        borderRadius: theme.radius.lg,
        backgroundColor: theme.colors.dangerSoft,
        borderWidth: 1,
        borderColor: 'rgba(249, 115, 115, 0.35)',
    },
    errorText: { color: theme.colors.danger, ...getTextStyle('body') },

    listContent: {
        paddingHorizontal: theme.spacing.lg,
        paddingTop: theme.spacing.md,
        paddingBottom: theme.spacing.md,
    },

    row: { marginBottom: theme.spacing.sm, flexDirection: 'row' },
    rowUser: { justifyContent: 'flex-end' },
    rowAi: { justifyContent: 'flex-start' },

    bubble: {
        maxWidth: '86%',
        borderRadius: theme.radius.lg,
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.sm,
        borderWidth: 1,
    },
    bubbleUser: {
        backgroundColor: theme.colors.primarySoft,
        borderColor: 'rgba(52, 211, 153, 0.30)',
    },
    bubbleAi: {
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        borderColor: theme.colors.border,
    },

    msgText: { ...getTextStyle('body'), lineHeight: 20 },
    msgTextUser: { color: theme.colors.text },
    msgTextAi: { color: theme.colors.text },

    typingRow: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: theme.spacing.sm,
        paddingTop: theme.spacing.sm,
        paddingBottom: theme.spacing.xs,
    },
    typingText: { color: theme.colors.textMuted, ...getTextStyle('caption') },

    suggestions: { marginTop: theme.spacing.sm, gap: theme.spacing.sm },
    suggestionCard: {
        backgroundColor: 'rgba(255,255,255,0.04)',
        borderRadius: theme.radius.lg,
        padding: theme.spacing.md,
        borderWidth: 1,
        borderColor: theme.colors.border,
    },
    suggestionTitle: {
        color: theme.colors.text,
        ...getTextStyle('bodyMedium'),
        marginBottom: 4,
    },
    suggestionReason: {
        color: theme.colors.textMuted,
        ...getTextStyle('caption'),
        marginBottom: theme.spacing.sm,
        lineHeight: 16,
    },
    suggestionButton: { alignSelf: 'flex-start' },

    inputBar: {
        flexDirection: 'row',
        alignItems: 'flex-end',
        paddingHorizontal: theme.spacing.lg,
        paddingVertical: theme.spacing.md,
        borderTopWidth: 1,
        borderTopColor: theme.colors.border,
        backgroundColor: 'rgba(2, 6, 23, 0.55)',
        gap: theme.spacing.sm,
    },
    input: {
        flex: 1,
        minHeight: 44,
        maxHeight: 120,
        paddingHorizontal: theme.spacing.md,
        paddingVertical: theme.spacing.sm,
        borderRadius: theme.radius.pill,
        borderWidth: 1,
        borderColor: theme.colors.border,
        color: theme.colors.text,
        backgroundColor: 'rgba(255,255,255,0.04)',
        ...getTextStyle('body'),
    },
    sendBtn: {
        paddingHorizontal: theme.spacing.lg,
        paddingVertical: theme.spacing.sm,
        borderRadius: theme.radius.pill,
        backgroundColor: theme.colors.primarySoft,
        borderWidth: 1,
        borderColor: 'rgba(52, 211, 153, 0.35)',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: 44,
    },
    sendText: { color: theme.colors.primary, ...getTextStyle('bodyMedium') },
});

export default AIScreen;
