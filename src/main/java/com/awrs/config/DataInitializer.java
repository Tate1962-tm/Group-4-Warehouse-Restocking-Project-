package com.awrs.config;

import com.awrs.model.Item;
import com.awrs.model.Role;
import com.awrs.model.User;
import com.awrs.model.WarehouseLocation;
import com.awrs.repository.ItemRepository;
import com.awrs.repository.UserRepository;
import com.awrs.repository.WarehouseLocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               ItemRepository itemRepository,
                               WarehouseLocationRepository locationRepository) {
        return args -> {
            userRepository.save(new User("admin", "admin123", Role.ADMIN));
            userRepository.save(new User("manager", "manager123", Role.MANAGER));
            userRepository.save(new User("worker", "worker123", Role.WORKER));

            itemRepository.save(new Item("SKU-001", "Widget A", "Acme Supply", "EA", 10, 50));
            itemRepository.save(new Item("SKU-002", "Gadget B", "Global Parts", "EA", 5, 30));

            WarehouseLocation aisle = new WarehouseLocation("WH1-A1", "Aisle 1", "AISLE", null);
            WarehouseLocation shelf = new WarehouseLocation("WH1-A1-S1", "Shelf 1", "SHELF", aisle);
            locationRepository.save(aisle);
            locationRepository.save(shelf);
            locationRepository.save(new WarehouseLocation("WH1-A1-S1-B1", "Bin 1", "BIN", shelf));
        };
    }
}
