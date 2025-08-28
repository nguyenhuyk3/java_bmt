package com.bmt.java_bmt.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmt.java_bmt.entities.FoodAndBeverage;

public interface IFoodAndBeverageRepository extends JpaRepository<FoodAndBeverage, UUID> {}
