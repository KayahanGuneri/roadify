package com.roadify.tripplanner.api;

import com.roadify.tripplanner.api.dto.CreateTripRequest;
import com.roadify.tripplanner.api.dto.TripResponse;
import com.roadify.tripplanner.api.dto.UpdateTripStopsRequest;
import com.roadify.tripplanner.application.service.TripServiceImpl;
import com.roadify.tripplanner.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TripController.class)
@Import(SecurityConfig.class)
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripServiceImpl tripServiceImpl;

    @Test
    void createTrip_withJwt_shouldReturn201Created_andUseSubAsUserId() throws Exception {
        // Arrange
        TripResponse expected = new TripResponse(
                "trip-1", "user-1", "route-1", "Title", Instant.now(), List.of()
        );

        when(tripServiceImpl.createTrip(anyString(), any(CreateTripRequest.class)))
                .thenReturn(expected);

        // Act + Assert
        mockMvc.perform(post("/v1/trips")
                        .with(jwt().jwt(jwt -> jwt.subject("user-1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "routeId": "route-1",
                                  "title": "Title"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // Verify userId passed from JWT sub
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(tripServiceImpl).createTrip(userIdCaptor.capture(), any(CreateTripRequest.class));
        assertEquals("user-1", userIdCaptor.getValue());
        verifyNoMoreInteractions(tripServiceImpl);
    }

    @Test
    void getTrip_withJwt_shouldReturn200Ok_andUseSubAsUserId() throws Exception {
        // Arrange
        TripResponse expected = new TripResponse(
                "trip-1", "user-1", "route-1", "Title", Instant.now(), List.of()
        );

        when(tripServiceImpl.getTrip(eq("user-1"), eq("trip-1")))
                .thenReturn(expected);

        // Act + Assert
        mockMvc.perform(get("/v1/trips/trip-1")
                        .with(jwt().jwt(jwt -> jwt.subject("user-1"))))
                .andExpect(status().isOk());

        verify(tripServiceImpl).getTrip("user-1", "trip-1");
        verifyNoMoreInteractions(tripServiceImpl);
    }

    @Test
    void updateStops_withJwt_shouldReturn200Ok_andUseSubAsUserId() throws Exception {
        // Arrange
        TripResponse expected = new TripResponse(
                "trip-1", "user-1", "route-1", "Title", Instant.now(), List.of()
        );

        when(tripServiceImpl.updateStops(anyString(), anyString(), any(UpdateTripStopsRequest.class)))
                .thenReturn(expected);

        // Act + Assert
        mockMvc.perform(put("/v1/trips/trip-1/stops")
                        .with(jwt().jwt(jwt -> jwt.subject("user-1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "add": [],
                                  "remove": []
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(tripServiceImpl).updateStops(userIdCaptor.capture(), eq("trip-1"), any(UpdateTripStopsRequest.class));
        assertEquals("user-1", userIdCaptor.getValue());
        verifyNoMoreInteractions(tripServiceImpl);
    }

    @Test
    @WithAnonymousUser
    void createTrip_withoutJwt_shouldReturn401() throws Exception {
        mockMvc.perform(post("/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "routeId": "route-1",
                                  "title": "Title"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(tripServiceImpl);
    }
}
