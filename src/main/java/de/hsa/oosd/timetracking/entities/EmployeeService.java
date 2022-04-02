package de.hsa.oosd.timetracking.entities;

import de.hsa.oosd.timetracking.models.DashboardInfo;
import de.hsa.oosd.timetracking.models.DatePeriod;
import de.hsa.oosd.timetracking.models.IdNameItem;
import de.hsa.oosd.timetracking.security.SecurityService;
import de.hsa.oosd.timetracking.utils.FindingUtils;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static de.hsa.oosd.timetracking.utils.DateTimePeriodUtils.*;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DailyStuffRepository dailyStuffRepository;
    private final AbsenceRepository absenceRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeInfoRepository employeeInfoRepository;
    private final FindingUtils findingUtils;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            SecurityService securityService,
            DailyStuffRepository dailyStuffRepository,
            AbsenceRepository absenceRepository,
            TimeEntryRepository timeEntryRepository,
            ProjectRepository projectRepository,
            EmployeeInfoRepository employeeInfoRepository,
            CustomUserRepository customUserRepository)
    {
        this.employeeRepository = employeeRepository;
        this.dailyStuffRepository = dailyStuffRepository;
        this.absenceRepository = absenceRepository;
        this.timeEntryRepository = timeEntryRepository;
        this.projectRepository = projectRepository;
        this.employeeInfoRepository = employeeInfoRepository;

        this.findingUtils = new FindingUtils(employeeRepository, securityService, customUserRepository);
    }

    public DashboardInfo getDashboardInfo() throws NotFoundException {
        var loggedInUser = findingUtils.getLoggedInUser();
        var user = findingUtils.getLoggedInCustomUser();
        var emp = findEmployeeByUsername(loggedInUser.getUsername());

        if (emp == null)
            return new DashboardInfo("User Unknown");
        var empInfo = employeeInfoRepository.findEmployeeInfoByOrganization(emp.getCustomUser().getOrganization());

        if (empInfo == null)
            return new DashboardInfo(findingUtils.getEmployeeFullName());

        var info =  new DashboardInfo(
                findingUtils.getEmployeeFullName(),
                user.getOrganization().getName(),
                emp.getPosition(),
                calcTargetHoursInMonth((int)empInfo.getWorkingHoursPerWeek()),
                countTotalHours(getWorkedHoursInMonth(emp, LocalDate.now())),
                (int)empInfo.getVacationDays(),
                countTotalDaysInPeriods(getAbsenceByYear(LocalDate.now().getYear()), true),
                getNextWorkingDay(empInfo, LocalDate.now())
        );

        info.quoteOfTheDay = getQuoteOfTheDay();
        info.projects = new ArrayList<>() {{
            add(new IdNameItem(-1L, "NO PROJECT"));
        }};
        info.projects.addAll(projectRepository.findByOrganization(user.getOrganization()).stream().map(o ->
                new IdNameItem(o.getId(), o.getName())).collect(Collectors.toList()));

        return info;
    }

    public void applyAbsence(LocalDate start, LocalDate end) throws Exception {
        var employee = findingUtils.getLoggedInEmployee();
        var absence = new Absence(employee, start, end);
        try {
            absenceRepository.save(absence);
        } catch (Exception ex) {
            throw new Exception(ex.getCause());
        }
    }

    public void addTimeEntry(LocalDate date, LocalTime startTime, LocalTime endTime, long projectId) throws Exception {
        var employee = findingUtils.getLoggedInEmployee();
        var project = projectRepository.findProjectById(projectId);
        var timeEntry = new TimeEntry(date, startTime,endTime, employee, project);
        var existingTimeEntries = timeEntryRepository.findAllByEmployeeAndDateOfWork(employee, date);

        if (isOverlapping(timeEntry, existingTimeEntries)) {
            throw new Exception("Your entered working hours overlap with existing time entries");
        }

        try {
            timeEntryRepository.save(timeEntry);
        } catch (Exception ex) {
            throw new Exception(ex.getCause());
        }
    }

    public List<TimeEntry> getAllTimeEntriesByEmployee() throws NotFoundException {
        var employee = findingUtils.getLoggedInEmployee();
        return timeEntryRepository.findAllByEmployee(employee);
    }

    public void deleteTimeEntry(long timeEntryId) {
        timeEntryRepository.deleteById(timeEntryId);
    }

    public void updateTimeEntry(TimeEntry timeEntry) throws Exception {
        var employee = findingUtils.getLoggedInEmployee();
        var existingTimeEntries = timeEntryRepository.findAllByEmployeeAndDateOfWork(employee, timeEntry.getDateOfWork());
        if (isOverlapping(timeEntry, existingTimeEntries)) {
            throw new Exception("Your entered working hours overlap with existing time entries");
        }
        else {
            timeEntry.update(timeEntry.getDateOfWork(), timeEntry.getStartTime(), timeEntry.getEndTime(), timeEntry.getProject());
            timeEntryRepository.save(timeEntry);
        }
    }

    public void saveEmployee(Employee employee){
        employeeRepository.save(employee);
    }

    private List<DatePeriod> getAbsenceByYear(int year) {
        var empAbsence = absenceRepository.findAllByEmployee_CustomUser_Username(findingUtils.getLoggedInUser().getUsername());
        return empAbsence.stream().filter(h -> h.getStart().getYear() == year || h.getEnd().getYear() == year)
                .map(h -> new DatePeriod(h.getStart(), h.getEnd())).collect(Collectors.toList());
    }

    // TODO: Returns simplified value. Calculation should be done dependent on month and working week-days
    private int calcTargetHoursInMonth(int workingHoursPerWeek) {
        return workingHoursPerWeek * 4;
    }

    private List<TimeEntry> getWorkedHoursInMonth(Employee employee, LocalDate month) {
        var start = month.withDayOfMonth(1);
        var end = month.withDayOfMonth(month.lengthOfMonth());

        return timeEntryRepository.findAllByEmployee(employee)
                .stream().filter(e ->(e.getDateOfWork().isEqual(start) || e.getDateOfWork().isAfter(start))
                                &&
                                (e.getDateOfWork().isEqual(end) || e.getDateOfWork().isBefore(end)))
                .collect(Collectors.toList());
    }

    private Employee findEmployeeByUsername(String username) {
        return employeeRepository.findEmployeeByCustomUser_Username(username);
    }

    private String getQuoteOfTheDay() {
        var dailyStuff = dailyStuffRepository.findFirstByDay(LocalDate.now());
        if (dailyStuff != null && !dailyStuff.getQuoteOfTheDay().isEmpty()) {
            return dailyStuff.getQuoteOfTheDay();
        }

        return "Eat. Sleep. Work. Repeat";
    }

    // TODO: It would be better to return a LocalDate
    private String getNextWorkingDay(EmployeeInfo info, LocalDate referenceDate) {
        var workingDays = workingDaysAsArray(info);
        var currentWeekDay = referenceDate.getDayOfWeek().getValue();

        for (int i = 0; i < workingDays.length; i++) {
            if (currentWeekDay > 6)
                currentWeekDay = 0;

            if (workingDays[currentWeekDay])
                break;

            currentWeekDay++;
        }

        return DayOfWeek.of(currentWeekDay + 1).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    private boolean[] workingDaysAsArray(EmployeeInfo info) {
        var workingDays = new boolean[7];
        workingDays[0] = info.isMonday();
        workingDays[1] = info.isTuesday();
        workingDays[2] = info.isWednesday();
        workingDays[3] = info.isThursday();
        workingDays[4] = info.isFriday();
        workingDays[5] = info.isSaturday();
        workingDays[6] = info.isSunday();

        return workingDays;
    }
}
