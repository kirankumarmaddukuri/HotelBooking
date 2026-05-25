package com.demo.HotelBooking.config;

import com.demo.HotelBooking.model.Hotel;
import com.demo.HotelBooking.model.Role;
import com.demo.HotelBooking.model.Room;
import com.demo.HotelBooking.model.User;
import com.demo.HotelBooking.repository.HotelRepository;
import com.demo.HotelBooking.repository.RoomRepository;
import com.demo.HotelBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@hotel.com").isEmpty()) {
            System.out.println("Seeding Admin User...");
            User admin = new User();
            admin.setEmail("admin@hotel.com");
            admin.setName("System Admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (hotelRepository.count() == 0) {
            System.out.println("Seeding Database with Indian Hotels and Rooms...");

            Hotel hotel1 = new Hotel();
            hotel1.setName("The Taj Mahal Palace");
            hotel1.setLocation("Mumbai, Maharashtra");
            hotel1.setDescription("Iconic luxury 5-star hotel overlooking the Gateway of India.");
            hotel1.setAmenities("Free Wi-Fi, Infinity Pool, Jiva Spa, 24/7 Butler Service");
            hotelRepository.save(hotel1);

            Hotel hotel2 = new Hotel();
            hotel2.setName("The Leela Palace");
            hotel2.setLocation("New Delhi, Delhi");
            hotel2.setDescription("A majestic 5-star property offering royal Indian hospitality.");
            hotel2.setAmenities("Free Breakfast, Rooftop Pool, ESPA, Fine Dining");
            hotelRepository.save(hotel2);
            
            Hotel hotel3 = new Hotel();
            hotel3.setName("ITC Grand Chola");
            hotel3.setLocation("Chennai, Tamil Nadu");
            hotel3.setDescription("A palatial luxury hotel celebrating the grandeur of the Chola Dynasty.");
            hotel3.setAmenities("10 Dining Options, Kaya Kalp Spa, Fitness Center, Helipad");
            hotelRepository.save(hotel3);

            Hotel hotel4 = new Hotel();
            hotel4.setName("Taj Falaknuma Palace");
            hotel4.setLocation("Hyderabad, Telangana");
            hotel4.setDescription("Live like royalty in the former palace of the Nizam, perched 2,000 feet above the city.");
            hotel4.setAmenities("Heritage Walks, Fine Dining, Jiva Spa, Infinity Pool");
            hotelRepository.save(hotel4);

            Hotel hotel5 = new Hotel();
            hotel5.setName("Novotel Varun");
            hotel5.setLocation("Vijayawada, Andhra Pradesh");
            hotel5.setDescription("Modern upscale hotel offering panoramic views of the city and premium comfort.");
            hotel5.setAmenities("Rooftop Track, Swimming Pool, Fitness Center, Free Wi-Fi");
            hotelRepository.save(hotel5);

            Hotel hotel6 = new Hotel();
            hotel6.setName("The Oberoi");
            hotel6.setLocation("Bengaluru, Karnataka");
            hotel6.setDescription("Nestled in lush tropical gardens in the heart of the city, offering a serene escape.");
            hotel6.setAmenities("Award-winning Spa, 24-hour Business Center, Personal Butler, Private Balconies");
            hotelRepository.save(hotel6);

            Hotel hotel7 = new Hotel();
            hotel7.setName("Rambagh Palace");
            hotel7.setLocation("Jaipur, Rajasthan");
            hotel7.setDescription("Experience the finest traditions of Rajput hospitality in the authentic 'Jewel of Jaipur'.");
            hotel7.setAmenities("Polo Golf, Vintage Car Rides, Royal Dining, Spa");
            hotelRepository.save(hotel7);

            Hotel hotel8 = new Hotel();
            hotel8.setName("W Goa");
            hotel8.setLocation("Vagator, Goa");
            hotel8.setDescription("Vibrant, beachfront luxury resort perfect for tropical getaways and parties.");
            hotel8.setAmenities("Rock Pool, AWAY Spa, Beach Access, Pet Friendly");
            hotelRepository.save(hotel8);

            // Add Rooms to Hotel 1 (Taj)
            List<Room> rooms1 = new ArrayList<>();
            rooms1.add(new Room(null, "Luxury Sea View", new BigDecimal("25000.00"), true, hotel1, null));
            rooms1.add(new Room(null, "Taj Club City View", new BigDecimal("18000.00"), true, hotel1, null));
            rooms1.add(new Room(null, "Rajput Suite", new BigDecimal("85000.00"), true, hotel1, null));
            roomRepository.saveAll(rooms1);

            // Add Rooms to Hotel 2 (Leela)
            List<Room> rooms2 = new ArrayList<>();
            rooms2.add(new Room(null, "Grand Premiere Room", new BigDecimal("22000.00"), true, hotel2, null));
            rooms2.add(new Room(null, "Royal Club Parlour", new BigDecimal("35000.00"), true, hotel2, null));
            roomRepository.saveAll(rooms2);

            // Add Rooms to Hotel 3 (ITC)
            List<Room> rooms3 = new ArrayList<>();
            rooms3.add(new Room(null, "Executive Club", new BigDecimal("12000.00"), true, hotel3, null));
            rooms3.add(new Room(null, "Chola Suite", new BigDecimal("45000.00"), true, hotel3, null));
            roomRepository.saveAll(rooms3);

            // Add Rooms to Hotel 4 (Hyderabad)
            List<Room> rooms4 = new ArrayList<>();
            rooms4.add(new Room(null, "Palace Room", new BigDecimal("32000.00"), true, hotel4, null));
            rooms4.add(new Room(null, "Nizam Suite", new BigDecimal("95000.00"), true, hotel4, null));
            roomRepository.saveAll(rooms4);

            // Add Rooms to Hotel 5 (Vijayawada)
            List<Room> rooms5 = new ArrayList<>();
            rooms5.add(new Room(null, "Superior Room", new BigDecimal("6500.00"), true, hotel5, null));
            rooms5.add(new Room(null, "Executive Suite", new BigDecimal("15000.00"), true, hotel5, null));
            roomRepository.saveAll(rooms5);

            // Add Rooms to Hotel 6 (Bengaluru)
            List<Room> rooms6 = new ArrayList<>();
            rooms6.add(new Room(null, "Deluxe Room", new BigDecimal("18500.00"), true, hotel6, null));
            rooms6.add(new Room(null, "Premier Suite", new BigDecimal("42000.00"), true, hotel6, null));
            roomRepository.saveAll(rooms6);

            // Add Rooms to Hotel 7 (Jaipur)
            List<Room> rooms7 = new ArrayList<>();
            rooms7.add(new Room(null, "Historical Room", new BigDecimal("28000.00"), true, hotel7, null));
            rooms7.add(new Room(null, "Royal Suite", new BigDecimal("120000.00"), true, hotel7, null));
            roomRepository.saveAll(rooms7);

            // Add Rooms to Hotel 8 (Goa)
            List<Room> rooms8 = new ArrayList<>();
            rooms8.add(new Room(null, "Wonderful Room", new BigDecimal("16000.00"), true, hotel8, null));
            rooms8.add(new Room(null, "Fantastic Chalet", new BigDecimal("38000.00"), true, hotel8, null));
            roomRepository.saveAll(rooms8);

            System.out.println("Database Seeding Completed!");
        }
    }
}
