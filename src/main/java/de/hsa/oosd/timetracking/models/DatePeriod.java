package de.hsa.oosd.timetracking.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class DatePeriod {
    @Getter
    @Setter
    private LocalDate start;

    @Getter
    @Setter
    private LocalDate end;

    public DatePeriod(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }
}
