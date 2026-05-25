package com.demo.HotelBooking.service;

import com.demo.HotelBooking.model.Hotel;
import com.demo.HotelBooking.model.BookingStatus;
import com.demo.HotelBooking.model.Room;
import com.demo.HotelBooking.repository.HotelRepository;
import com.demo.HotelBooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public List<Hotel> searchHotels(String location) {
        return hotelRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Hotel> searchHotels(String location, LocalDate checkInDate, LocalDate checkOutDate) {
        return hotelRepository.findByLocationContainingIgnoreCase(location).stream()
                .filter(hotel -> !getAvailableRoomsByHotelId(hotel.getId(), checkInDate, checkOutDate).isEmpty())
                .collect(Collectors.toList());
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public List<Room> getAvailableRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public List<Room> getAvailableRoomsByHotelId(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepository.findAvailableRoomsByHotelIdAndDateRange(
                hotelId,
                checkInDate,
                checkOutDate,
                BookingStatus.CANCELLED
        );
    }

    public Hotel createHotel(com.demo.HotelBooking.dto.HotelRequest request) {
        Hotel hotel = new Hotel();
        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setDescription(request.getDescription());
        hotel.setAmenities(request.getAmenities());
        
        Hotel savedHotel = hotelRepository.save(hotel);
        
        if (request.getRooms() != null && !request.getRooms().isEmpty()) {
            List<Room> rooms = request.getRooms().stream().map(roomReq -> {
                Room room = new Room();
                room.setRoomType(roomReq.getRoomType());
                room.setPrice(roomReq.getPrice());
                room.setIsAvailable(roomReq.getIsAvailable() != null ? roomReq.getIsAvailable() : true);
                room.setHotel(savedHotel);
                return room;
            }).collect(Collectors.toList());
            
            roomRepository.saveAll(rooms);
        }
        
        return savedHotel;
    }

    public Room addRoomToHotel(Long hotelId, com.demo.HotelBooking.dto.RoomRequest roomReq) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        Room room = new Room();
        room.setRoomType(roomReq.getRoomType());
        room.setPrice(roomReq.getPrice());
        room.setIsAvailable(roomReq.getIsAvailable() != null ? roomReq.getIsAvailable() : true);
        room.setHotel(hotel);

        return roomRepository.save(room);
    }
}
