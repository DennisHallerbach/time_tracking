package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
public class Absence {
    @GeneratedValue
    @Column(nullable = false)
    @Id
    @Getter
    private int id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @Getter
    private Employee employee;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate start;

    @GeneratedValue
    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate end;

    public Absence(Employee employee, LocalDate start, LocalDate end) {
        this.employee = employee;
        this.start = start;
        this.end = end;
    }
}
