package com.bmt.java_bmt.controllers;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.responses.foodAndBeverage.GetAllFoodAndBeverageResponse;
import com.bmt.java_bmt.services.IFoodAndBeverageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/food-and-beverage")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodAndBeverageController {
    IFoodAndBeverageService foodAndBeverageService;

    @GetMapping()
    public APIResponse<Set<GetAllFoodAndBeverageResponse>> getAll() {
        var result = foodAndBeverageService.getAll();

        return APIResponse.<Set<GetAllFoodAndBeverageResponse>>builder()
                .result(result)
                .build();
    }
}
