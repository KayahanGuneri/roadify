package com.roadify.tripplanner.application.service;

import com.roadify.tripplanner.api.dto.CreateTripRequest;
import com.roadify.tripplanner.api.dto.TripResponse;
import com.roadify.tripplanner.api.dto.TripStopDto;
import com.roadify.tripplanner.api.dto.UpdateTripStopsRequest;
import com.roadify.tripplanner.application.exception.TripNotFoundException;
import com.roadify.tripplanner.application.port.TripEventPublisher;
import com.roadify.tripplanner.application.port.TripRepository;
import com.roadify.tripplanner.application.port.TripService;
import com.roadify.tripplanner.application.port.TripStopRepository;
import com.roadify.tripplanner.domain.Trip;
import com.roadify.tripplanner.domain.TripStop;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripStopRepository tripStopRepository;
    private final TripEventPublisher eventPublisher;

    public TripServiceImpl(
            TripRepository tripRepository,
            TripStopRepository tripStopRepository,
            TripEventPublisher eventPublisher
    ) {
        this.tripRepository = tripRepository;
        this.tripStopRepository = tripStopRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public TripResponse createTrip(String userId, CreateTripRequest request) {
        validateCreateTripRequest(request);

        Trip trip = Trip.createNew(
                userId,
                request.getRouteId().trim(),
                request.getTitle().trim(),
                Instant.now()
        );

        tripRepository.save(trip);

        eventPublisher.publishTripCreated(
                trip.getId().toString(),
                userId,
                trip.getRouteId()
        );

        // Yeni trip oluşturulduğunda henüz stop yok
        return toTripResponse(trip, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTrip(String userId, String tripId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        List<TripStop> stops = tripStopRepository.findByTripIdAndUserId(tripId, userId);
        return toTripResponse(trip, stops);
    }

    @Override
    @Transactional
    public TripResponse updateStops(String userId, String tripId, UpdateTripStopsRequest request) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new TripNotFoundException(tripId));

        List<String> removeIds = nullSafeList(request.getRemoveIds());
        List<TripStopDto> addDtos = nullSafeList(request.getAdd());

        // 1) Silinecek stop’lar
        if (!removeIds.isEmpty()) {
            tripStopRepository.deleteByTripIdAndIds(tripId, removeIds);
            eventPublisher.publishTripStopsRemoved(tripId, userId, removeIds);
        }

        // 2) Eklenecek / güncellenecek stop’lar
        if (!addDtos.isEmpty()) {
            // Aynı orderIndex için birden fazla DTO gelmişse, sonuncuyu kabul edelim.
            List<TripStopDto> normalizedAddDtos = deduplicateByOrderIndex(addDtos);

            List<TripStop> newStops = normalizedAddDtos.stream()
                    .map(dto -> toNewTripStop(tripId, dto))
                    .toList();

            // ON CONFLICT (trip_id, order_index) DO UPDATE sayesinde
            // burada duplicate key hatası yerine UPSERT çalışacak.
            tripStopRepository.saveAll(newStops);

            List<String> addedStopIds = newStops.stream()
                    .map(TripStop::getId)
                    .toList();

            eventPublisher.publishTripStopsAdded(tripId, userId, addedStopIds);
        }

        // 3) Güncel listeyi dön
        List<TripStop> stops = tripStopRepository.findByTripIdAndUserId(tripId, userId);
        return toTripResponse(trip, stops);
    }

    /**
     * Aynı orderIndex'e sahip birden fazla DTO varsa
     * (örneğin client yanlışlıkla iki kez gönderirse),
     * sonuncuyu koruyoruz.
     */
    private static List<TripStopDto> deduplicateByOrderIndex(List<TripStopDto> addDtos) {
        // orderIndex set’i ile filter
        // (en son görülen DTO’yu almak için tersten iterate de edilebilir;
        // burada basit bir çözüm kullanıyoruz).
        Set<Integer> seen = addDtos.stream()
                .map(TripStopDto::getOrderIndex)
                .collect(Collectors.toSet());

        // Basit yaklaşım: zaten list çok büyük değil; distinct-by-key gibi düşünülebilir.
        // Dilersen burayı daha deterministik bir "sonuncuyu al" mantığına çevirebiliriz.
        return addDtos.stream()
                .collect(Collectors.toMap(
                        TripStopDto::getOrderIndex,
                        dto -> dto,
                        (first, second) -> second // çatışmada ikinciyi al
                ))
                .values()
                .stream()
                .toList();
    }

    private static TripStop toNewTripStop(String tripId, TripStopDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Stop to add must not be null");
        }
        if (dto.getPlaceId() == null || dto.getPlaceId().isBlank()) {
            throw new IllegalArgumentException("placeId must not be blank");
        }
        if (dto.getOrderIndex() < 0) {
            throw new IllegalArgumentException("orderIndex must be >= 0");
        }

        return TripStop.createNew(
                tripId,
                dto.getPlaceId().trim(),
                dto.getPlaceName(), // placeName nullable
                dto.getOrderIndex(),
                dto.getPlannedArrivalTime(),
                dto.getPlannedDurationMinutes()
        );
    }

    private static void validateCreateTripRequest(CreateTripRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }
        if (request.getRouteId() == null || request.getRouteId().isBlank()) {
            throw new IllegalArgumentException("routeId must not be blank");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }

    private static <T> List<T> nullSafeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static TripResponse toTripResponse(Trip trip, List<TripStop> stops) {
        Objects.requireNonNull(trip, "trip must not be null");

        List<TripStopDto> stopDtos = (stops == null ? List.<TripStop>of() : stops).stream()
                .sorted(Comparator.comparingInt(TripStop::getOrderIndex))
                .map(s -> new TripStopDto(
                        s.getId(),
                        s.getTripId(),
                        s.getPlaceId(),
                        s.getPlaceName().orElse(null),
                        s.getOrderIndex(),
                        s.getPlannedArrivalTime().orElse(null),
                        s.getPlannedDurationMinutes().orElse(null)
                ))
                .toList();

        return new TripResponse(
                trip.getId().toString(),
                trip.getUserId(),
                trip.getRouteId(),
                trip.getTitle(),
                trip.getCreatedAt(),
                stopDtos
        );
    }
}
