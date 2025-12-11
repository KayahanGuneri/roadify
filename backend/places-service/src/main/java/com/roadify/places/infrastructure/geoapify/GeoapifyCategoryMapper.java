package com.roadify.places.infrastructure.geoapify;

import com.roadify.places.domain.PlaceCategory;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps domain-level PlaceCategory to Geoapify "categories" strings.
 */
public final class GeoapifyCategoryMapper {

    private static final Map<PlaceCategory, List<String>> CATEGORY_MAP = Map.of(
            PlaceCategory.FOOD, List.of(
                    "catering.restaurant",
                    "catering.fast_food"
            ),
            PlaceCategory.CAFE, List.of(
                    "catering.cafe"
            ),
            PlaceCategory.FUEL, List.of(
                    // IMPORTANT: correct Geoapify category
                    "service.vehicle.fuel"
            ),
            PlaceCategory.TOURIST, List.of(
                    "tourism.sights",
                    "tourism.attraction"
            ),
            PlaceCategory.MARKET, List.of(
                    "commercial.supermarket",
                    "commercial.marketplace"
            ),
            PlaceCategory.WC, List.of(
                    "amenity.toilet"
            ),
            PlaceCategory.HOTEL, List.of(
                    "accommodation.hotel",
                    "accommodation.guest_house"
            ),
            PlaceCategory.CAMPING, List.of(
                    "camping.camp_site",
                    "camping.caravan_site"
            ),
            PlaceCategory.SHOP, List.of(
                    "commercial.convenience",
                    "commercial.shopping_mall"
            ),
            PlaceCategory.OTHER, List.of(
                    // Safe fallback – you can tune this
                    "amenity",
                    "commercial"
            )
    );

    private GeoapifyCategoryMapper() {
    }

    /**
     * Build the comma-separated "categories" parameter for Geoapify API.
     * If categories is empty or null, you can interpret it as "ALL".
     */
    public static String buildCategoriesParam(EnumSet<PlaceCategory> categories) {
        EnumSet<PlaceCategory> effective =
                (categories == null || categories.isEmpty())
                        ? EnumSet.allOf(PlaceCategory.class)
                        : categories;

        return effective.stream()
                .flatMap(cat -> CATEGORY_MAP.getOrDefault(cat, List.<String>of()).stream())
                .distinct()
                .collect(Collectors.joining(","));
    }
}
