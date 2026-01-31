import React, { useEffect, useMemo, useRef } from 'react';
import MapView, { Marker, Polyline, LatLng } from 'react-native-maps';
import polyline from '@mapbox/polyline';
import type { RouteDTO } from '../types/routes';

type Props = {
    route: RouteDTO;
    onReady?: () => void;
};

export const RouteMap: React.FC<Props> = ({ route, onReady }) => {
    const mapRef = useRef<MapView | null>(null);
    const fittedOnceRef = useRef(false);

    const fallbackLine: LatLng[] = useMemo(
        () => [
            { latitude: route.fromLat, longitude: route.fromLng },
            { latitude: route.toLat, longitude: route.toLng },
        ],
        [route.fromLat, route.fromLng, route.toLat, route.toLng]
    );

    const coordinates: LatLng[] = useMemo(() => {
        if (!route.geometry) return fallbackLine;

        try {
            const decoded: [number, number][] = polyline.decode(route.geometry);
            if (!decoded?.length) return fallbackLine;
            return decoded.map(([lat, lon]) => ({ latitude: lat, longitude: lon }));
        } catch (e) {
            console.warn('[RouteMap] polyline decode failed, fallback line', e);
            return fallbackLine;
        }
    }, [route.geometry, fallbackLine]);

    // Fit once when we have coords
    useEffect(() => {
        if (!mapRef.current) return;
        if (coordinates.length < 2) return;
        if (fittedOnceRef.current) return;

        fittedOnceRef.current = true;

        // Delay 1 frame for layout stability
        requestAnimationFrame(() => {
            mapRef.current?.fitToCoordinates(coordinates, {
                edgePadding: { top: 90, bottom: 280, left: 40, right: 40 },
                animated: true,
            });
        });
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
                latitudeDelta: Math.max(Math.abs(route.fromLat - route.toLat) * 1.5, 2),
                longitudeDelta: Math.max(Math.abs(route.fromLng - route.toLng) * 1.5, 2),
            }}
            onMapReady={() => {
                // MapView ready signal
                onReady?.();
            }}
            onLayout={() => {
                // Some Android builds trigger this earlier; it's fine as a second signal
                onReady?.();
            }}
        >
            {coordinates.length > 1 ? (
                <Polyline coordinates={coordinates} strokeWidth={4} />
            ) : null}

            <Marker coordinate={fromCoord} title="From" />
            <Marker coordinate={toCoord} title="To" />
        </MapView>
    );
};
