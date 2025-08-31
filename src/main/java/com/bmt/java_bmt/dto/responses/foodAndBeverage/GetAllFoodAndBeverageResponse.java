package com.bmt.java_bmt.dto.responses.foodAndBeverage;

import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllFoodAndBeverageResponse {
    UUID id;
    String name;
    int price;
    String imageUrl;
}
