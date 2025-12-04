
import { useQuery } from '@tanstack/react-query';
import { getRouteById } from '../api/routes';
import { RouteDTO } from '../types/routes';

/**
 * Hook for fetching a route by its id.
 * Will be used on RoutePreviewScreen.
 */
export function useRouteById(routeId: string) {
    return useQuery<RouteDTO, Error>({
        queryKey: ['route', routeId],
        queryFn: () => getRouteById(routeId),
        enabled: !!routeId,
        staleTime: 1000 * 60,
        retry: 1,
    });
}
