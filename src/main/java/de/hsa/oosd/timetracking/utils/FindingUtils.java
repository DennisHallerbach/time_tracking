package de.hsa.oosd.timetracking.utils;

import de.hsa.oosd.timetracking.entities.CustomUser;
import de.hsa.oosd.timetracking.entities.CustomUserRepository;
import de.hsa.oosd.timetracking.entities.Employee;
import de.hsa.oosd.timetracking.entities.EmployeeRepository;
import de.hsa.oosd.timetracking.security.SecurityService;
import javassist.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

public class FindingUtils {
    private final EmployeeRepository employeeRepository;
    private final SecurityService securityService;
    private final CustomUserRepository customUserRepository;

    public FindingUtils(EmployeeRepository employeeRepository, SecurityService securityService, CustomUserRepository customUserRepository) {
        this.employeeRepository = employeeRepository;
        this.securityService = securityService;
        this.customUserRepository = customUserRepository;
    }

    public Employee getLoggedInEmployee() throws NotFoundException {
        var employee = employeeRepository.findEmployeeByCustomUser_Username(getLoggedInUser().getUsername());
        if (employee == null)
            throw new NotFoundException("Employee not found");

        return employee;
    }

    public CustomUser getLoggedInCustomUser() throws NotFoundException {
        var user = customUserRepository.findCustomUserByUsername(getLoggedInUser().getUsername());
        if (user == null)
            throw new NotFoundException("User not found");

        return user;
    }

    public String getEmployeeFullName() {
        try {
            var employee = getLoggedInEmployee();
            return String.format("%s %s", employee.getFirstName(), employee.getLastName());
        } catch (NotFoundException e) {
            return "Unknown User";
        }
    }

    public UserDetails getLoggedInUser() {
        var loggedInUser = securityService.getAuthenticatedUser();
        if (loggedInUser == null)
            throw new SecurityException("No authenticated user");

        return loggedInUser;
    }
}
