package com.bmt.java_bmt.implementations;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.responses.foodAndBeverage.GetAllFoodAndBeverageResponse;
import com.bmt.java_bmt.repositories.IFoodAndBeverageRepository;
import com.bmt.java_bmt.services.IFoodAndBeverageService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@AllArgsConstructor
public class FoodAndBeverageImpl implements IFoodAndBeverageService {
    IFoodAndBeverageRepository foodAndBeverageRepository;

    @Override
    public Set<GetAllFoodAndBeverageResponse> getAll() {
        return foodAndBeverageRepository.findAll().stream()
                .filter(fab -> !fab.getIsDeleted())
                .map(fab -> GetAllFoodAndBeverageResponse.builder()
                        .id(fab.getId())
                        .name(fab.getName())
                        .price(fab.getPrice())
                        .imageUrl(fab.getImageUrl())
                        .build())
                .collect(Collectors.toSet());
    }
}
