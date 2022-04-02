package de.hsa.oosd.timetracking.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyStuffRepository extends JpaRepository<DailyStuff, Integer> {
    DailyStuff findFirstByDay(LocalDate day);
}