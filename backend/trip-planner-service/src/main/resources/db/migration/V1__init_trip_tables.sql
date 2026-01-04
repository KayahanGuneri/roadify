-- V1__init_trip_tables.sql

CREATE SCHEMA IF NOT EXISTS trip_planner;
SET search_path TO trip_planner;
CREATE TABLE IF NOT EXISTS trips (
                                     id UUID PRIMARY KEY,
                                     user_id TEXT NOT NULL,
                                     route_id TEXT NOT NULL,
                                     title TEXT NOT NULL,
                                     created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_trips_user_id_created_at
    ON trips (user_id, created_at DESC);

CREATE TABLE IF NOT EXISTS trip_stops (
                                          id UUID PRIMARY KEY,
                                          trip_id UUID NOT NULL,
                                          place_id TEXT NOT NULL,
                                          order_index INT NOT NULL,
                                          planned_arrival_time TIMESTAMPTZ NULL,
                                          planned_duration_minutes INT NULL,

                                          CONSTRAINT fk_trip_stops_trip_id
                                          FOREIGN KEY (trip_id)
    REFERENCES trips (id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_trip_stops_trip_id_order_index
    ON trip_stops (trip_id, order_index);

CREATE UNIQUE INDEX IF NOT EXISTS uq_trip_stops_trip_id_order_index
    ON trip_stops (trip_id, order_index);
