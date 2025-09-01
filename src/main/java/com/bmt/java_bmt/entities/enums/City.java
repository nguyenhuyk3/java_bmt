package com.bmt.java_bmt.entities.enums;

import java.util.Map;

public enum City {
    HO_CHI_MINH,
    HA_NOI,
    DONG_NAI;

    public static final Map<City, String> CITY_TO_VIETNAMESE = Map.of(
            City.HO_CHI_MINH, "Hồ Chí Minh",
            City.HA_NOI, "Hà Nội",
            City.DONG_NAI, "Đồng Nai");

    public static String toVietnamese(City city) {
        return CITY_TO_VIETNAMESE.getOrDefault(city, "Không xác định");
    }
}
