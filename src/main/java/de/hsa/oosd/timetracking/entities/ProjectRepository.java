package de.hsa.oosd.timetracking.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    Project findProjectByName(String name);
    Project findProjectById(long id);
    List<Project> findByOrganization(Organization organization);
}