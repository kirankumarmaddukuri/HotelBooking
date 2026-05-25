package com.demo.HotelBooking.repository;

import com.demo.HotelBooking.model.BookingStatus;
import com.demo.HotelBooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByHotelIdAndIsAvailableTrue(Long hotelId);

    @Query("""
            select r from Room r
            where r.hotel.id = :hotelId
            and r.id not in (
                select b.room.id from Booking b
                where b.status <> :cancelledStatus
                and b.checkInDate < :checkOutDate
                and b.checkOutDate > :checkInDate
            )
            """)
    List<Room> findAvailableRoomsByHotelIdAndDateRange(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("cancelledStatus") BookingStatus cancelledStatus
    );
}
