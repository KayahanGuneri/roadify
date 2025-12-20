package com.roadify.tripplanner.api.dto;

import java.util.List;

/**
 * Patch payload for updating trip stops:
 * - add: new stops to add
 * - removeIds: existing stop ids to remove
 */
public class UpdateTripStopsRequest {

    private List<TripStopDto> add;
    private List<String> removeIds;

    public UpdateTripStopsRequest() {
    }

    public UpdateTripStopsRequest(List<TripStopDto> add, List<String> removeIds) {
        this.add = add;
        this.removeIds = removeIds;
    }

    public List<TripStopDto> getAdd() {
        return add;
    }

    public void setAdd(List<TripStopDto> add) {
        this.add = add;
    }

    public List<String> getRemoveIds() {
        return removeIds;
    }

    public void setRemoveIds(List<String> removeIds) {
        this.removeIds = removeIds;
    }
}
