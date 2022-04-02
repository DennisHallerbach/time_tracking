package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class EmployeeInfo {
    @GeneratedValue
    @Column(nullable = false)
    @Id
    @Getter
    private int id;

    @OneToOne
    @Getter
    @JoinColumn(name = "organizationID")
    @NonNull
    private Organization organization;

    @Column(nullable = false)
    @Getter
    @Setter
    private String workingTimeModel = "model1";

    @Column(nullable = false)
    @Getter
    @Setter
    private double workingHoursPerWeek = 40; // geplante Arbeitszeit pro Woche

    @Column(nullable = false)
    @Getter
    @Setter
    private double numDaysPerWeek = 5; // Anzahl der geplanten Arbeitstage pro Woche

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isMonday = true; // Arbeitstage

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isTuesday = true;

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isWednesday = true;

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isThursday = true;

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isFriday = true;

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isSaturday = false;

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isSunday = false;

    @Column(nullable = false)
    @Getter
    @Setter
    private double vacationDays = 30; // Anzahl der Urlaubstage pro Jahr

    public EmployeeInfo(Organization orga){
        this.organization = orga;
    }
}
