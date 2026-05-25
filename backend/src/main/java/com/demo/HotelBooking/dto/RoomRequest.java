package com.demo.HotelBooking.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomRequest {
    private String roomType;
    private BigDecimal price;
    private Boolean isAvailable = true;
}
