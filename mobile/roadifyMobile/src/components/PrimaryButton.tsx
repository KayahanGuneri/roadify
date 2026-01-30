import React from 'react';
import { Text, StyleSheet, ViewStyle } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { getElevation, getTextStyle, theme } from '../theme/theme';
import { PressableScale } from './PressableScale';

type Props = {
  title: string;
  onPress: () => void;
  style?: ViewStyle;
  disabled?: boolean;
};

export const PrimaryButton: React.FC<Props> = ({ title, onPress, style, disabled }) => {
  return (
      <PressableScale
          onPress={onPress}
          disabled={disabled}
          style={style}
          contentStyle={styles.pressable}
          hitSlop={{ top: 6, bottom: 6, left: 6, right: 6 }}
      >
        <LinearGradient colors={[theme.colors.primary, '#22C55E']} style={[styles.button, getElevation('e2')]}>
          <Text style={styles.text}>{title}</Text>
        </LinearGradient>
      </PressableScale>
  );
};

const styles = StyleSheet.create({
  pressable: {
    borderRadius: theme.radius.xl,
    overflow: 'hidden',
  },
  button: {
    paddingVertical: theme.spacing.sm + 4,
    paddingHorizontal: theme.spacing.lg,
    borderRadius: theme.radius.xl,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    color: theme.colors.text,
    ...getTextStyle('bodyMedium'),
  },
});
