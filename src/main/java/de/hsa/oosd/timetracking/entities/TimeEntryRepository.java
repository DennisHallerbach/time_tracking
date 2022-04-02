package de.hsa.oosd.timetracking.entities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository <TimeEntry, Long>  {
    List<TimeEntry> findAllByEmployee(Employee employee);
    List<TimeEntry> findAllByEmployeeAndDateOfWork(Employee employee, LocalDate dateOfWork);
}
