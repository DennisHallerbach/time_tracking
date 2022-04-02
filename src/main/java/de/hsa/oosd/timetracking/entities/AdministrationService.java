package de.hsa.oosd.timetracking.entities;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdministrationService {
    private final EmployeeRepository employeeRepository;
    private final DailyStuffRepository dailyStuffRepository;

    public AdministrationService(EmployeeRepository employeeRepository, DailyStuffRepository dailyStuffRepository) {
        this.employeeRepository = employeeRepository;
        this.dailyStuffRepository = dailyStuffRepository;
    }

    public void createEmployee(String name, String surname, String email, CustomUser user) {
        var employee = new Employee(name, surname, email, user);
        employeeRepository.save(employee);
    }

    public void deleteEmployee(Employee employee) {
        employeeRepository.delete(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> findAllEmployeesByLastName(String lastName) {
        return employeeRepository.findAllByLastName(lastName);
    }

    public void addQuoteOfTheDay(LocalDate day, String quoteOfTheDay) throws Exception {
        var dailyStuff = new DailyStuff();
        dailyStuff.setDay(day);
        dailyStuff.setQuoteOfTheDay(quoteOfTheDay);

        try {
            dailyStuffRepository.save(dailyStuff);
        }catch (Exception e ){
            throw new Exception(e.getCause());
        }
    }
}
