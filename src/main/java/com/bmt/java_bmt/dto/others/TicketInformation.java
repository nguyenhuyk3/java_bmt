package com.bmt.java_bmt.dto.others;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketInformation {
    String filmTitle;
    String genres;
    String duration;
    String posterUrl;
    String cinemaName;
    String city;
    String address;
    String auditorium;
    String showDate;
    String showTime;
    String seats;
    List<FABItem> FABItems;
}
