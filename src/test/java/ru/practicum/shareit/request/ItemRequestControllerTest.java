package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.errors.exception.BadParameterException;
import ru.practicum.shareit.errors.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.of;
import static java.time.Month.OCTOBER;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final LocalDateTime time = of(1999, OCTOBER, 1, 1, 1, 1);
    private final String headerSharerUserId = "X-Sharer-User-Id";
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("I need this one!")
            .requesterId(1L)
            .created(time)
            .items(null)
            .build();
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.save(any(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .characterEncoding(UTF_8))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId().intValue())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items", nullValue()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequestsById() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .header(headerSharerUserId, 1))
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestDto.getRequesterId().intValue())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].items", nullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{requestId}", 1)
                        .header(headerSharerUserId, 1))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId().intValue())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items", nullValue()))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestNotFoundExceptionTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyInt(), anyLong())).thenThrow(ObjectNotFoundException.class);
        mvc.perform(get("/requests/{requestId}", 1).header(headerSharerUserId, 27))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestsWithNotFoundException() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong())).thenThrow(ObjectNotFoundException.class);

        mvc.perform(get("/requests")
                        .header(headerSharerUserId, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveItemRequestValidationExceptionTest() throws Exception {
        when(itemRequestService.save(any(), anyLong())).thenThrow(BadParameterException.class);
        mvc.perform(post("/requests")
                        .header(headerSharerUserId, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}