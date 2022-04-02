package de.hsa.oosd.timetracking.entities;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    Organization findOrganizationByName(String name);


}
