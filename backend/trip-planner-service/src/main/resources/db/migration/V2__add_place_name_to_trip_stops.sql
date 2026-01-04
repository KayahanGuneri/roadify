-- Adds place_name to trip_stops to display human-readable names in Trip UI.
-- Safe to re-run (IF NOT EXISTS).

ALTER TABLE trip_planner.trip_stops
    ADD COLUMN IF NOT EXISTS place_name TEXT NULL;

CREATE INDEX IF NOT EXISTS idx_trip_stops_place_name
    ON trip_planner.trip_stops (place_name);
