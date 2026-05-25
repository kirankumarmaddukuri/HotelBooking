package com.demo.HotelBooking.dto;

import lombok.Data;
import java.util.List;

@Data
public class HotelRequest {
    private String name;
    private String location;
    private String description;
    private String amenities;
    private List<RoomRequest> rooms;
}
