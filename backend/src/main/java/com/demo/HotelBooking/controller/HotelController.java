package com.demo.HotelBooking.controller;

import com.demo.HotelBooking.model.Hotel;
import com.demo.HotelBooking.model.Room;
import com.demo.HotelBooking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LocalDate checkInDate,
            @RequestParam(required = false) LocalDate checkOutDate
    ) {
        if (checkInDate != null && checkOutDate != null) {
            return ResponseEntity.ok(hotelService.searchHotels(location == null ? "" : location, checkInDate, checkOutDate));
        }

        if (location != null && !location.isEmpty()) {
            return ResponseEntity.ok(hotelService.searchHotels(location));
        }
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/rooms")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate checkInDate,
            @RequestParam(required = false) LocalDate checkOutDate
    ) {
        if (checkInDate != null && checkOutDate != null) {
            return ResponseEntity.ok(hotelService.getAvailableRoomsByHotelId(id, checkInDate, checkOutDate));
        }

        return ResponseEntity.ok(hotelService.getAvailableRoomsByHotelId(id));
    }

    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody com.demo.HotelBooking.dto.HotelRequest request) {
        return ResponseEntity.ok(hotelService.createHotel(request));
    }

    @PostMapping("/{id}/rooms")
    public ResponseEntity<Room> addRoomToHotel(@PathVariable Long id, @RequestBody com.demo.HotelBooking.dto.RoomRequest request) {
        return ResponseEntity.ok(hotelService.addRoomToHotel(id, request));
    }
}
