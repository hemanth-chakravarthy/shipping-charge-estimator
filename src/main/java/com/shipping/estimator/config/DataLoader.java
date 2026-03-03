package com.shipping.estimator.config;

import com.shipping.estimator.entity.Customer;
import com.shipping.estimator.entity.Product;
import com.shipping.estimator.entity.Seller;
import com.shipping.estimator.entity.Warehouse;
import com.shipping.estimator.repository.CustomerRepository;
import com.shipping.estimator.repository.ProductRepository;
import com.shipping.estimator.repository.SellerRepository;
import com.shipping.estimator.repository.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

        private final SellerRepository sellerRepository;
        private final CustomerRepository customerRepository;
        private final WarehouseRepository warehouseRepository;
        private final ProductRepository productRepository;

        public DataLoader(SellerRepository sellerRepository,
                        CustomerRepository customerRepository,
                        WarehouseRepository warehouseRepository,
                        ProductRepository productRepository) {
                this.sellerRepository = sellerRepository;
                this.customerRepository = customerRepository;
                this.warehouseRepository = warehouseRepository;
                this.productRepository = productRepository;
        }

        @Override
        public void run(String... args) {
                // Seed Sellers
                Seller seller1 = sellerRepository.save(Seller.builder()
                                .name("Sharma Traders")
                                .latitude(28.6139) // Delhi
                                .longitude(77.2090)
                                .build());

                Seller seller2 = sellerRepository.save(Seller.builder()
                                .name("Mumbai Distributors")
                                .latitude(19.0760) // Mumbai
                                .longitude(72.8777)
                                .build());

                Seller seller3 = sellerRepository.save(Seller.builder()
                                .name("Chennai Wholesale")
                                .latitude(13.0827) // Chennai
                                .longitude(80.2707)
                                .build());

                // Seed Warehouses
                warehouseRepository.save(Warehouse.builder()
                                .name("North Warehouse - Delhi")
                                .latitude(28.7041)
                                .longitude(77.1025)
                                .build());

                warehouseRepository.save(Warehouse.builder()
                                .name("West Warehouse - Mumbai")
                                .latitude(19.1136)
                                .longitude(72.8697)
                                .build());

                warehouseRepository.save(Warehouse.builder()
                                .name("South Warehouse - Bangalore")
                                .latitude(12.9716)
                                .longitude(77.5946)
                                .build());

                // Seed Customers
                customerRepository.save(Customer.builder()
                                .name("Rajesh Kirana Store")
                                .phoneNumber("+91-9876543210")
                                .latitude(26.8467) // Lucknow
                                .longitude(80.9462)
                                .build());

                customerRepository.save(Customer.builder()
                                .name("Suresh General Store")
                                .phoneNumber("+91-9123456789")
                                .latitude(23.0225) // Ahmedabad
                                .longitude(72.5714)
                                .build());

                customerRepository.save(Customer.builder()
                                .name("Priya Provision Store")
                                .phoneNumber("+91-9988776655")
                                .latitude(17.3850) // Hyderabad
                                .longitude(78.4867)
                                .build());

                // Seed Products
                productRepository.save(Product.builder()
                                .seller(seller1)
                                .name("Rice (25kg bag)")
                                .weightKg(25.0)
                                .lengthCm(60.0)
                                .widthCm(40.0)
                                .heightCm(20.0)
                                .price(1200.0)
                                .build());

                productRepository.save(Product.builder()
                                .seller(seller2)
                                .name("Cooking Oil (10L)")
                                .weightKg(9.5)
                                .lengthCm(30.0)
                                .widthCm(20.0)
                                .heightCm(35.0)
                                .price(950.0)
                                .build());

                productRepository.save(Product.builder()
                                .seller(seller3)
                                .name("Sugar (50kg bag)")
                                .weightKg(50.0)
                                .lengthCm(70.0)
                                .widthCm(45.0)
                                .heightCm(25.0)
                                .price(2200.0)
                                .build());

                System.out.println("=======================================================");
                System.out.println("✅ Sample data loaded successfully!");
                System.out.println("   Sellers   : " + sellerRepository.count());
                System.out.println("   Customers : " + customerRepository.count());
                System.out.println("   Warehouses: " + warehouseRepository.count());
                System.out.println("   Products  : " + productRepository.count());
                System.out.println("=======================================================");
        }
}
