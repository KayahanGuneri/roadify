// src/components/RouteMap.tsx
import React, { useEffect, useMemo, useRef } from 'react';
import MapView, { Marker, Polyline, LatLng } from 'react-native-maps';
import polyline from '@mapbox/polyline';
import { RouteDTO } from '../types/routes';

type Props = {
    route: RouteDTO;
};

export const RouteMap: React.FC<Props> = ({ route }) => {
    const mapRef = useRef<MapView | null>(null);

    const coordinates: LatLng[] = useMemo(() => {
        // geometry yoksa / bozulduysa düz çizgiye düş
        if (!route.geometry) {
            return [
                { latitude: route.fromLat, longitude: route.fromLng },
                { latitude: route.toLat, longitude: route.toLng },
            ];
        }

        try {
            // geometry artık raw encoded polyline string
            const decoded: [number, number][] = polyline.decode(route.geometry);

            if (!decoded || decoded.length === 0) {
                // güvenlik: yine fallback
                return [
                    { latitude: route.fromLat, longitude: route.fromLng },
                    { latitude: route.toLat, longitude: route.toLng },
                ];
            }

            return decoded.map(([lat, lon]) => ({
                latitude: lat,
                longitude: lon,
            }));
        } catch (e) {
            console.warn('Failed to decode polyline, fallback to straight line', e);
            return [
                { latitude: route.fromLat, longitude: route.fromLng },
                { latitude: route.toLat, longitude: route.toLng },
            ];
        }
    }, [route]);

    // Kamera fitBounds
    useEffect(() => {
        if (mapRef.current && coordinates.length > 1) {
            mapRef.current.fitToCoordinates(coordinates, {
                edgePadding: { top: 80, bottom: 260, left: 40, right: 40 },
                animated: true,
            });
        }
    }, [coordinates]);

    const fromCoord = { latitude: route.fromLat, longitude: route.fromLng };
    const toCoord = { latitude: route.toLat, longitude: route.toLng };

    return (
        <MapView
            ref={mapRef}
            style={{ flex: 1 }}
            initialRegion={{
                latitude: (route.fromLat + route.toLat) / 2,
                longitude: (route.fromLng + route.toLng) / 2,
                latitudeDelta: Math.abs(route.fromLat - route.toLat) * 1.5 || 5,
                longitudeDelta: Math.abs(route.fromLng - route.toLng) * 1.5 || 5,
            }}
        >
            {/* çizgi */}
            {coordinates.length > 1 && (
                <Polyline
                    coordinates={coordinates}
                    strokeWidth={4}
                    strokeColor="#10B981"
                />
            )}

            {/* marker'lar */}
            <Marker coordinate={fromCoord} title="From" />
            <Marker coordinate={toCoord} title="To" />
        </MapView>
    );
};
