package de.hsa.oosd.timetracking.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Integer> {
    List<Absence> findAllByEmployee_CustomUser_Username(String userName);
}