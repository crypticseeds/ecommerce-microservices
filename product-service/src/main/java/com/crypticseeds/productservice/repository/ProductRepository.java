package com.crypticseeds.productservice.repository;

import com.crypticseeds.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
