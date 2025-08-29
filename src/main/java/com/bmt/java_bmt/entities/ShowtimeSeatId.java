package com.bmt.java_bmt.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ShowtimeSeatId implements Serializable {
    // SerialVersionUID là bắt buộc cho các lớp Id
    private static final long serialVersionUID = -5382089458992383189L;

    @Column(name = "sh_id")
    private UUID showtimeId;

    @Column(name = "se_id")
    private UUID seatId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ShowtimeSeatId that = (ShowtimeSeatId) o;

        return Objects.equals(showtimeId, that.showtimeId) && Objects.equals(seatId, that.seatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(showtimeId, seatId);
    }
}
