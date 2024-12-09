package com.crypticseeds.inventoryservice;

import com.crypticseeds.inventoryservice.model.Inventory;
import com.crypticseeds.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory inventory1 = new Inventory();
            inventory1.setSkuCode("iphone_16");
            inventory1.setQuantity(80);

            Inventory inventory2 = new Inventory();
            inventory2.setSkuCode("iphone_16_pro");
            inventory2.setQuantity(50);

            Inventory inventory3 = new Inventory();
            inventory3.setSkuCode("iphone_16_pro_max");
            inventory3.setQuantity(20);

            inventoryRepository.save(inventory1);
            inventoryRepository.save(inventory2);
            inventoryRepository.save(inventory3);
        };
    }

}
