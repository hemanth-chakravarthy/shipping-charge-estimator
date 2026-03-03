package com.shipping.estimator.repository;

import com.shipping.estimator.entity.Product;
import com.shipping.estimator.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findBySeller(Seller seller);
}
