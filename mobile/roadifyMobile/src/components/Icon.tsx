import React from 'react';
import { Image, ImageStyle } from 'react-native';

type Props = {
    name: string;
    size?: number;
    style?: ImageStyle;
};

const iconMap: any = {
    home: require('../assets/icons/home.png'),
    ai: require('../assets/icons/ai.png'),
    trips: require('../assets/icons/trips.png'),
    analytics: require('../assets/icons/analytics.png'),
    route: require('../assets/icons/route.png'),
    pin: require('../assets/icons/map-pin.png'),
    location: require('../assets/icons/location.png'),
    back: require('../assets/icons/back.png'),
};

export const Icon = ({ name, size = 24, style }: Props) => (
    <Image
        source={iconMap[name]}
        style={[{ width: size, height: size, resizeMode: 'contain' }, style]}
    />
);
