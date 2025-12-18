package com.roadify.places.infrastructure.provider;

import com.roadify.places.domain.Place;
import com.roadify.places.domain.PlaceCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class SimplePlaceNormalizer implements PlaceNormalizer {

    @Override
    public List<Place> normalize(List<RawPlace> rawPlaces, String routeGeometry) {
        if (rawPlaces == null || rawPlaces.isEmpty()) return List.of();

        return rawPlaces.stream()
                .map(raw -> {
                    PlaceCategory category = pickBestCategory(raw);
                    return Place.builder()
                            .id(buildId(raw))
                            .name(raw.getName())
                            .category(category)
                            .latitude(raw.getLatitude())
                            .longitude(raw.getLongitude())
                            .rating(raw.getRating())
                            .detourKm(0.0) // sende sonrasında detour hesaplıyorsun, o kısım ayrı
                            .build();
                })
                .toList();
    }

    /**
     * Geoapify categories listesinde en uygun kategoriyi seç.
     * Overpass gibi tek categoryTag dönenlerde categoryTag üzerinden ilerler.
     */
    private PlaceCategory pickBestCategory(RawPlace raw) {
        // 1) categories listesi varsa hepsini tara (en kritik fix)
        if (raw.getCategories() != null && !raw.getCategories().isEmpty()) {
            // Öncelik sırası: FOOD -> CAFE -> FUEL -> HOTEL -> CAMPING -> MARKET/SHOP -> WC -> TOURIST -> OTHER
            PlaceCategory c;

            c = findInList(raw.getCategories(), PlaceCategory.FOOD);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.CAFE);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.FUEL);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.HOTEL);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.CAMPING);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.MARKET);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.SHOP);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.WC);
            if (c != null) return c;

            c = findInList(raw.getCategories(), PlaceCategory.TOURIST);
            if (c != null) return c;

            return PlaceCategory.OTHER;
        }

        // 2) categories yoksa tek tag’e düş
        return mapCategory(raw.getCategoryTag());
    }

    private PlaceCategory findInList(List<String> categories, PlaceCategory target) {
        for (String s : categories) {
            if (s == null) continue;
            PlaceCategory mapped = mapCategory(s);
            if (mapped == target) return target;
        }
        return null;
    }

    @Override
    public PlaceCategory mapCategory(String providerCategory) {
        if (providerCategory == null) return PlaceCategory.OTHER;

        String tag = providerCategory.toLowerCase(Locale.ROOT);

        // FOOD
        if (tag.contains("catering.restaurant") || tag.contains("restaurant")
                || tag.contains("fast_food") || tag.contains("catering.fast_food")
                || tag.contains("food")) return PlaceCategory.FOOD;

        // CAFE
        if (tag.contains("catering.cafe") || tag.contains("cafe") || tag.contains("coffee")) return PlaceCategory.CAFE;

        // FUEL
        if (tag.contains("service.vehicle.fuel") || tag.contains("fuel_station")
                || tag.contains("fuel") || tag.contains("gas") || tag.contains("petrol")) return PlaceCategory.FUEL;

        // HOTEL
        if (tag.contains("accommodation.hotel") || tag.contains("guest_house")
                || tag.contains("hotel") || tag.contains("motel")) return PlaceCategory.HOTEL;

        // CAMPING
        if (tag.contains("camping.camp_site") || tag.contains("camping.caravan_site")
                || tag.contains("camp") || tag.contains("camping")) return PlaceCategory.CAMPING;

        // MARKET / SHOP
        if (tag.contains("commercial.supermarket") || tag.contains("supermarket") || tag.contains("marketplace"))
            return PlaceCategory.MARKET;

        if (tag.contains("commercial.shopping_mall") || tag.contains("shopping_mall")
                || tag.contains("shop") || tag.contains("commercial.convenience"))
            return PlaceCategory.SHOP;

        // WC
        if (tag.contains("amenity.toilet") || tag.contains("wc") || tag.contains("toilet") || tag.contains("restroom"))
            return PlaceCategory.WC;

        // TOURIST
        if (tag.contains("tourism.attraction") || tag.contains("tourism.sights")
                || tag.contains("attraction") || tag.contains("museum") || tag.contains("viewpoint"))
            return PlaceCategory.TOURIST;

        return PlaceCategory.OTHER;
    }

    private String buildId(RawPlace raw) {
        return raw.getProvider() + ":" + raw.getExternalId();
    }
}
