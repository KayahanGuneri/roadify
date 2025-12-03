package com.roadify.route.domain;

import java.util.Optional;

/**
 * Repository abstraction for persisting and loading routes.
 */
public interface RouteRepository {

    /**
     * Persist the given route.
     */
    void save(Route route);

    /**
     * Find a route by its id.
     *
     * @param id route identifier (UUID as String)
     * @return optional containing the route if found
     */
    Optional<Route> findById(String id);
}
