package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
public class DailyStuff {
    @GeneratedValue
    @Column(nullable = false)
    @Id
    @Getter
    private int id;

    @Column()
    @Getter
    @Setter
    private LocalDate day;

    @Column()
    @Getter
    @Setter
    private String quoteOfTheDay;

}
