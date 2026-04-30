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
        private final com.shipping.estimator.repository.UserRepository userRepository;
        private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

        public DataLoader(SellerRepository sellerRepository,
                        CustomerRepository customerRepository,
                        WarehouseRepository warehouseRepository,
                        ProductRepository productRepository,
                        com.shipping.estimator.repository.UserRepository userRepository,
                        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
                this.sellerRepository = sellerRepository;
                this.customerRepository = customerRepository;
                this.warehouseRepository = warehouseRepository;
                this.productRepository = productRepository;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) {
                // CORRECT DELETE ORDER: Delete children first
                productRepository.deleteAll();
                sellerRepository.deleteAll();
                customerRepository.deleteAll();
                warehouseRepository.deleteAll();
                userRepository.deleteAll();

                // Seed Admin User
                userRepository.save(com.shipping.estimator.entity.User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("password"))
                        .role("ROLE_ADMIN")
                        .build());

                // Seed Sellers
                Seller s1 = sellerRepository.save(Seller.builder().name("Sharma Traders (Delhi)").latitude(28.6139).longitude(77.2090).build());
                Seller s2 = sellerRepository.save(Seller.builder().name("Mumbai Distributors").latitude(19.0760).longitude(72.8777).build());
                Seller s3 = sellerRepository.save(Seller.builder().name("Chennai Wholesale").latitude(13.0827).longitude(80.2707).build());
                Seller s4 = sellerRepository.save(Seller.builder().name("Kerala Spices Co").latitude(9.9312).longitude(76.2673).build());
                Seller s5 = sellerRepository.save(Seller.builder().name("Gujarat Textiles").latitude(21.1702).longitude(72.8311).build());

                // Seed Products (30+ items)
                String[] categories = {"Electronics", "Home", "Kitchen", "Office", "Tools", "Garden"};
                double[] baseWeights = {2.5, 5.0, 1.5, 0.8, 10.0, 4.0};
                int[] basePrices = {15000, 2000, 1500, 800, 5000, 2500};

                int productCount = 0;
                Seller[] sellers = {s1, s2, s3, s4, s5};
                
                for (Seller seller : sellers) {
                    for (int i = 0; i < 7; i++) {
                        String cat = categories[i % categories.length];
                        productRepository.save(Product.builder()
                                .seller(seller)
                                .name(cat + " Item #" + (++productCount))
                                .weightKg(baseWeights[i % baseWeights.length] + Math.random())
                                .lengthCm(20 + (int)(Math.random() * 50))
                                .widthCm(20 + (int)(Math.random() * 30))
                                .heightCm(10 + (int)(Math.random() * 20))
                                .price(basePrices[i % basePrices.length] + (int)(Math.random() * 500))
                                .build());
                    }
                }

                // Add some specific high-volume items
                // Kirana / Grocery Items (Realistic low-weight daily goods)
                productRepository.save(Product.builder().seller(s3).name("Rice 5kg Bag").weightKg(5.0).lengthCm(30).widthCm(20).heightCm(10).price(350).build());
                productRepository.save(Product.builder().seller(s3).name("Wheat Flour (Atta) 5kg").weightKg(5.0).lengthCm(30).widthCm(20).heightCm(10).price(280).build());
                productRepository.save(Product.builder().seller(s4).name("Sugar 1kg").weightKg(1.0).lengthCm(15).widthCm(10).heightCm(5).price(45).build());
                productRepository.save(Product.builder().seller(s4).name("Salt 1kg").weightKg(1.0).lengthCm(15).widthCm(10).heightCm(5).price(20).build());
                productRepository.save(Product.builder().seller(s2).name("Toor Dal 1kg").weightKg(1.0).lengthCm(15).widthCm(10).heightCm(5).price(140).build());
                productRepository.save(Product.builder().seller(s2).name("Moong Dal 1kg").weightKg(1.0).lengthCm(15).widthCm(10).heightCm(5).price(120).build());
                productRepository.save(Product.builder().seller(s1).name("Cooking Oil 1L").weightKg(1.0).lengthCm(25).widthCm(10).heightCm(10).price(160).build());
                productRepository.save(Product.builder().seller(s1).name("Tea Powder 500g").weightKg(0.5).lengthCm(10).widthCm(8).heightCm(5).price(220).build());
                productRepository.save(Product.builder().seller(s5).name("Coffee Powder 200g").weightKg(0.2).lengthCm(8).widthCm(6).heightCm(4).price(180).build());
                productRepository.save(Product.builder().seller(s3).name("Biscuits Pack").weightKg(0.3).lengthCm(12).widthCm(8).heightCm(4).price(30).build());
                productRepository.save(Product.builder().seller(s2).name("Maggi Noodles Pack").weightKg(0.1).lengthCm(10).widthCm(8).heightCm(3).price(14).build());
                productRepository.save(Product.builder().seller(s4).name("Bath Soap").weightKg(0.2).lengthCm(10).widthCm(6).heightCm(3).price(35).build());
                productRepository.save(Product.builder().seller(s5).name("Shampoo Bottle 200ml").weightKg(0.25).lengthCm(15).widthCm(6).heightCm(5).price(120).build());
                productRepository.save(Product.builder().seller(s1).name("Toothpaste 150g").weightKg(0.15).lengthCm(15).widthCm(4).heightCm(3).price(95).build());
                productRepository.save(Product.builder().seller(s3).name("Detergent Powder 1kg").weightKg(1.0).lengthCm(20).widthCm(15).heightCm(5).price(110).build());
                // Heavy Equipment & Bulk Industrial Goods
                productRepository.save(Product.builder().seller(s1).name("Commercial Deep Freezer").weightKg(80.0).lengthCm(80).widthCm(70).heightCm(180).price(35000).build());
                productRepository.save(Product.builder().seller(s1).name("Industrial Air Conditioner Unit").weightKg(60.0).lengthCm(100).widthCm(60).heightCm(100).price(45000).build());
                productRepository.save(Product.builder().seller(s2).name("CNC Machine Parts").weightKg(45.0).lengthCm(60).widthCm(50).heightCm(40).price(25000).build());
                productRepository.save(Product.builder().seller(s2).name("Heavy Duty Lathe Machine").weightKg(150.0).lengthCm(200).widthCm(100).heightCm(150).price(120000).build());
                productRepository.save(Product.builder().seller(s3).name("Warehouse Storage Rack System").weightKg(30.0).lengthCm(120).widthCm(40).heightCm(60).price(8000).build());
                productRepository.save(Product.builder().seller(s3).name("Hydraulic Jack Set").weightKg(15.0).lengthCm(50).widthCm(30).heightCm(20).price(6000).build());
                productRepository.save(Product.builder().seller(s4).name("Commercial Refrigerator").weightKg(70.0).lengthCm(90).widthCm(75).heightCm(190).price(28000).build());
                productRepository.save(Product.builder().seller(s4).name("Printing Press Machine").weightKg(90.0).lengthCm(120).widthCm(80).heightCm(100).price(65000).build());
                productRepository.save(Product.builder().seller(s5).name("Power Loom Machine").weightKg(110.0).lengthCm(150).widthCm(90).heightCm(120).price(85000).build());
                productRepository.save(Product.builder().seller(s5).name("Industrial RO Plant").weightKg(55.0).lengthCm(70).widthCm(60).heightCm(90).price(32000).build());
                productRepository.save(Product.builder().seller(s1).name("Cold Storage Panel Set").weightKg(20.0).lengthCm(200).widthCm(100).heightCm(5).price(15000).build());
                productRepository.save(Product.builder().seller(s2).name("Bulk Spice Grinding Machine").weightKg(40.0).lengthCm(60).widthCm(50).heightCm(70).price(18000).build());
                productRepository.save(Product.builder().seller(s3).name("Oil Extraction Machine").weightKg(25.0).lengthCm(50).widthCm(40).heightCm(60).price(12000).build());
                productRepository.save(Product.builder().seller(s4).name("Commercial Juicer Machine").weightKg(18.0).lengthCm(45).widthCm(40).heightCm(60).price(9000).build());
                productRepository.save(Product.builder().seller(s5).name("Soap Making Machine").weightKg(22.0).lengthCm(55).widthCm(45).heightCm(65).price(11000).build());
                // Seed Warehouses
                warehouseRepository.save(Warehouse.builder().name("North Hub - Delhi").latitude(28.7041).longitude(77.1025).build());
                warehouseRepository.save(Warehouse.builder().name("West Hub - Mumbai").latitude(19.1136).longitude(72.8697).build());
                warehouseRepository.save(Warehouse.builder().name("South Hub - Bangalore").latitude(12.9716).longitude(77.5946).build());
                warehouseRepository.save(Warehouse.builder().name("East Hub - Kolkata").latitude(22.5726).longitude(88.3639).build());

                // Seed Customers (WITH PHONE NUMBERS)
                customerRepository.save(Customer.builder().name("Rajesh Kirana (Lucknow)").phoneNumber("+91-1111111111").latitude(26.8467).longitude(80.9462).build());
                customerRepository.save(Customer.builder().name("Suresh General (Ahmedabad)").phoneNumber("+91-2222222222").latitude(23.0225).longitude(72.5714).build());
                customerRepository.save(Customer.builder().name("Priya Provision (Hyderabad)").phoneNumber("+91-3333333333").latitude(17.3850).longitude(78.4867).build());
                customerRepository.save(Customer.builder().name("Amritsar Mart").phoneNumber("+91-4444444444").latitude(31.6340).longitude(74.8723).build());
                customerRepository.save(Customer.builder().name("Patna Mega Mart").phoneNumber("+91-5555555555").latitude(25.5941).longitude(85.1376).build());

                        System.out.println("=======================================================");
                        System.out.println("✅ Sample data loaded successfully!");
                        System.out.println("   Sellers   : " + sellerRepository.count());
                        System.out.println("   Customers : " + customerRepository.count());
                        System.out.println("   Warehouses: " + warehouseRepository.count());
                        System.out.println("   Products  : " + productRepository.count());
                        System.out.println("=======================================================");
        }
}
