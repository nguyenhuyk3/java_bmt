package com.bmt.java_bmt.services;

import java.util.Set;

import com.bmt.java_bmt.dto.responses.foodAndBeverage.GetAllFoodAndBeverageResponse;

public interface IFoodAndBeverageService {
    Set<GetAllFoodAndBeverageResponse> getAll();
}
