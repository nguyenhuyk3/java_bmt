package com.bmt.java_bmt.entities.enums;

import java.util.Map;

public enum Genre {
    ACTION,
    ADVENTURE,
    ANIMATION,
    COMEDY,
    CRIME,
    DRAMA,
    FANTASY,
    HISTORICAL,
    HORROR,
    MYSTERY,
    ROMANCE,
    SCI_FI,
    THRILLER,
    WAR,
    WESTERN,
    DARK_COMEDY,
    DOCUMENTARY,
    MUSICAL,
    SPORTS,
    SUPERHERO,
    PSYCHOLOGICAL_THRILLER,
    SLASHER,
    BIOPIC,
    NOIR,
    FAMILY;

    private static final Map<Genre, String> VIETNAMESE_NAMES = Map.ofEntries(
            Map.entry(ACTION, "Hành động"),
            Map.entry(ADVENTURE, "Phiêu lưu"),
            Map.entry(ANIMATION, "Hoạt hình"),
            Map.entry(COMEDY, "Hài"),
            Map.entry(CRIME, "Tội phạm"),
            Map.entry(DRAMA, "Chính kịch"),
            Map.entry(FANTASY, "Giả tưởng"),
            Map.entry(HISTORICAL, "Lịch sử"),
            Map.entry(HORROR, "Kinh dị"),
            Map.entry(MYSTERY, "Bí ẩn"),
            Map.entry(ROMANCE, "Lãng mạn"),
            Map.entry(SCI_FI, "Khoa học viễn tưởng"),
            Map.entry(THRILLER, "Giật gân"),
            Map.entry(WAR, "Chiến tranh"),
            Map.entry(WESTERN, "Viễn Tây"),
            Map.entry(DARK_COMEDY, "Hài đen"),
            Map.entry(DOCUMENTARY, "Tài liệu"),
            Map.entry(MUSICAL, "Nhạc kịch"),
            Map.entry(SPORTS, "Thể thao"),
            Map.entry(SUPERHERO, "Siêu anh hùng"),
            Map.entry(PSYCHOLOGICAL_THRILLER, "Kinh dị tâm lý"),
            Map.entry(SLASHER, "Kinh dị chém giết"),
            Map.entry(BIOPIC, "Tiểu sử"),
            Map.entry(NOIR, "Tội phạm noir"),
            Map.entry(FAMILY, "Gia đình"));

    public String getVietnameseName() {
        return VIETNAMESE_NAMES.get(this);
    }
}
