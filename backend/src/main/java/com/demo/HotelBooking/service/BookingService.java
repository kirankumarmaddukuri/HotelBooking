package com.demo.HotelBooking.service;

import com.demo.HotelBooking.dto.BookingRequest;
import com.demo.HotelBooking.model.Booking;
import com.demo.HotelBooking.model.BookingStatus;
import com.demo.HotelBooking.model.Room;
import com.demo.HotelBooking.model.User;
import com.demo.HotelBooking.repository.BookingRepository;
import com.demo.HotelBooking.repository.RoomRepository;
import com.demo.HotelBooking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Booking bookRoom(String email, BookingRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == com.demo.HotelBooking.model.Role.ADMIN) {
            throw new RuntimeException("Administrators are not allowed to book rooms.");
        }
        
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));

        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        boolean isAvailable = roomRepository.findAvailableRoomsByHotelIdAndDateRange(
                room.getHotel().getId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                BookingStatus.CANCELLED
        ).stream().anyMatch(availableRoom -> availableRoom.getId().equals(room.getId()));

        if (!isAvailable) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking {} created by {} for room {} from {} to {}", savedBooking.getId(), email, room.getId(),
                request.getCheckInDate(), request.getCheckOutDate());
        return savedBooking;
    }

    public List<Booking> getUserBookings(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByUserId(user.getId());
    }

    @Transactional
    public boolean cancelBooking(String email, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        boolean emailSent = emailService.sendUserInitiatedCancellation(user.getEmail(), booking.getId());
        logger.info("Booking {} cancelled by {}", bookingId, email);
        return emailSent;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public boolean updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        boolean emailSent = true;
        
        booking.setStatus(newStatus);
        
        if (newStatus == BookingStatus.CANCELLED) {
            emailSent = emailService.sendAdminInitiatedCancellation(booking.getUser().getEmail(), booking.getId());
        } else if (newStatus == BookingStatus.CONFIRMED) {
            emailSent = emailService.sendBookingConfirmation(booking.getUser().getEmail(), booking.getId(), booking.getRoom().getHotel().getName());
        }

        bookingRepository.save(booking);
        logger.info("Booking {} status updated to {}", bookingId, newStatus);
        return emailSent;
    }
}
