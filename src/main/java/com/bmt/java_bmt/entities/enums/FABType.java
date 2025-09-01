package com.bmt.java_bmt.entities.enums;

import java.util.Map;

public enum FABType {
    FOOD,
    BEVERAGE;

    public static final Map<FABType, String> FAB_TYPE_EMOJI = Map.of(
            FABType.FOOD, "\uD83C\uDF7F",
            FABType.BEVERAGE, "\uD83E\uDD64");

    public static String toEmoji(FABType type) {
        return FAB_TYPE_EMOJI.getOrDefault(type, "Không xác định");
    }
}
