package de.hsa.oosd.timetracking.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomUserRepository extends JpaRepository<CustomUser,Long> {
    CustomUser findCustomUserByUsername(String Username);

    List<CustomUser> findByOrganization(Organization organization);
}


