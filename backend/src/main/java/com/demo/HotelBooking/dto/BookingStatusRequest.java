package com.demo.HotelBooking.dto;

import com.demo.HotelBooking.model.BookingStatus;
import lombok.Data;

@Data
public class BookingStatusRequest {
    private BookingStatus status;
}
