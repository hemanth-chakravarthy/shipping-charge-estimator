package com.shipping.estimator.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MasterDataResponse {
    private List<SellerDTO> sellers;
    private List<CustomerDTO> customers;
    private List<WarehouseDTO> warehouses;
    private List<ProductDTO> products;

    @Data
    @Builder
    public static class SellerDTO {
        private UUID id;
        private String name;
        private double latitude;
        private double longitude;
    }

    @Data
    @Builder
    public static class CustomerDTO {
        private UUID id;
        private String name;
        private String phoneNumber;
        private double latitude;
        private double longitude;
    }

    @Data
    @Builder
    public static class WarehouseDTO {
        private UUID id;
        private String name;
        private double latitude;
        private double longitude;
    }

    @Data
    @Builder
    public static class ProductDTO {
        private UUID id;
        private String name;
        private UUID sellerId;
        private double weightKg;
        private double lengthCm;
        private double widthCm;
        private double heightCm;
        private double price;
    }
}
