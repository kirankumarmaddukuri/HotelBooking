package com.demo.HotelBooking.controller;

import com.demo.HotelBooking.dto.BookingRequest;
import com.demo.HotelBooking.dto.MessageResponse;
import com.demo.HotelBooking.model.Booking;
import com.demo.HotelBooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        try {
            Booking booking = bookingService.bookRoom(email, request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(bookingService.getUserBookings(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        try {
            boolean emailSent = bookingService.cancelBooking(email, id);
            if (!emailSent) {
                return ResponseEntity.ok(new MessageResponse("Booking cancelled successfully, but the cancellation email could not be sent."));
            }
            return ResponseEntity.ok(new MessageResponse("Booking cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody com.demo.HotelBooking.dto.BookingStatusRequest request) {
        try {
            boolean emailSent = bookingService.updateBookingStatus(id, request.getStatus());
            if (!emailSent && (request.getStatus() == com.demo.HotelBooking.model.BookingStatus.CONFIRMED
                    || request.getStatus() == com.demo.HotelBooking.model.BookingStatus.CANCELLED)) {
                return ResponseEntity.ok(new MessageResponse("Booking status updated to " + request.getStatus() + ", but the notification email could not be sent."));
            }
            return ResponseEntity.ok(new MessageResponse("Booking status updated to " + request.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
