// src/theme/theme.ts

/**
 * theme.ts
 *
 * English:
 * Single source of truth for Roadify UI tokens.
 * Provides semantic colors, spacing, typography, radius and elevation presets.
 *
 * Türkçe Özet:
 * Roadify için tek merkez tema dosyası. Renk/boşluk/yazı/radius/gölge presetleri burada.
 */

import { Platform, TextStyle, ViewStyle } from 'react-native';
import { colors as rawColors } from './colors';
import { spacing as rawSpacing } from './spacing';

type TypographyKey = 'title' | 'h1' | 'h2' | 'body' | 'bodyMedium' | 'caption' | 'overline';
type ElevationKey = 'e0' | 'e1' | 'e2' | 'e3';
type RadiusKey = 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'pill';

const radius = {
    sm: 10,
    md: 14,
    lg: 16,
    xl: 20,
    '2xl': 24,
    pill: 999,
} satisfies Record<RadiusKey, number>;

const spacing = {
    xs: rawSpacing.xs,
    sm: rawSpacing.sm,
    md: rawSpacing.md,
    lg: rawSpacing.lg,
    xl: rawSpacing.xl,
    '2xl': 40,
};

const colors = {
    // Backgrounds / surfaces
    bg: rawColors.navy,
    surface: '#0B1626', // slightly deeper than bg for depth
    card: rawColors.cardBg,

    // Text
    text: rawColors.text,
    textMuted: 'rgba(249,250,251,0.70)',

    // Lines
    border: 'rgba(255,255,255,0.10)',

    // Brand
    primary: rawColors.green,
    primarySoft: 'rgba(52,211,153,0.14)',

    // States
    danger: '#F97373',
    dangerSoft: 'rgba(249,115,115,0.14)',
};

const typography = {
    title: { fontSize: 28, fontWeight: '800', letterSpacing: 0.2 } as TextStyle,
    h1: { fontSize: 22, fontWeight: '800', letterSpacing: 0.2 } as TextStyle,
    h2: { fontSize: 18, fontWeight: '700' } as TextStyle,
    body: { fontSize: 14, fontWeight: '400' } as TextStyle,
    bodyMedium: { fontSize: 14, fontWeight: '600' } as TextStyle,
    caption: { fontSize: 12, fontWeight: '500' } as TextStyle,
    overline: { fontSize: 11, fontWeight: '700', letterSpacing: 0.8 } as TextStyle,
} satisfies Record<TypographyKey, TextStyle>;

const elevation = {
    e0: {} as ViewStyle,
    e1: Platform.select({
        ios: { shadowColor: '#000', shadowOpacity: 0.18, shadowOffset: { width: 0, height: 4 }, shadowRadius: 8 },
        android: { elevation: 2 },
        default: {},
    }) as ViewStyle,
    e2: Platform.select({
        ios: { shadowColor: '#000', shadowOpacity: 0.22, shadowOffset: { width: 0, height: 6 }, shadowRadius: 12 },
        android: { elevation: 4 },
        default: {},
    }) as ViewStyle,
    e3: Platform.select({
        ios: { shadowColor: '#000', shadowOpacity: 0.28, shadowOffset: { width: 0, height: 10 }, shadowRadius: 18 },
        android: { elevation: 7 },
        default: {},
    }) as ViewStyle,
} satisfies Record<ElevationKey, ViewStyle>;

export const theme = {
    colors,
    spacing,
    radius,
    typography,
    elevation,
};

export function getTextStyle(key: TypographyKey): TextStyle {
    return theme.typography[key];
}

export function getElevation(key: ElevationKey): ViewStyle {
    return theme.elevation[key];
}
