package de.hsa.oosd.timetracking.entities;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeInfoRepository extends JpaRepository<EmployeeInfo, Integer> {

        EmployeeInfo findEmployeeInfoByOrganization(Organization organization);

}
