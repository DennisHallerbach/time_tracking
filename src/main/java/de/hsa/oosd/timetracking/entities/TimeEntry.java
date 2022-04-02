package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor

public class TimeEntry {
    private LocalDate dateOfWork;
    private LocalTime startTime;
    private LocalTime endTime;
    @GeneratedValue
    @Column(nullable = false)
    @Id
    @Getter
    private Long id;

    @Getter
    @Setter
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "project_id")
    private Project project;

    public TimeEntry(LocalDate dateOfWork, LocalTime startTime, LocalTime endTime, Employee employee, Project project){
        this.dateOfWork = dateOfWork;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employee = employee;
        this.project = project;
    }
    public LocalDate getDateOfWork() {
        return dateOfWork;
    }

    public void setDateOfWork(LocalDate dateOfWork) {
        this.dateOfWork = dateOfWork;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getProjectName() {
        Project project = this.getProject();
        if (project == null)
            return "NO PROJECT";

        return project.getName();
    }

    public void update(LocalDate dateOfWork, LocalTime startTime, LocalTime endTime, Project project) {
        this.dateOfWork = dateOfWork;
        this.startTime = startTime;
        this.endTime = endTime;
        this.project = project;
    }
}
