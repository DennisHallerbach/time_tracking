package de.hsa.oosd.timetracking.utils;

import de.hsa.oosd.timetracking.entities.TimeEntry;
import de.hsa.oosd.timetracking.models.DatePeriod;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DateTimePeriodUtils {

    public static boolean isPeriodValid(LocalDate start, LocalDate end, boolean allowEqual) {
        return start != null && end != null && (start.isBefore(end) || (allowEqual && start.isEqual(end)));
    }

    public static boolean isPeriodValid(LocalTime start, LocalTime end, boolean allowEqual) {
        return start != null && end != null && (start.isBefore(end) || (allowEqual && start.compareTo(end) == 0));
    }

    public static int countTotalDaysInPeriods(List<DatePeriod> periods, boolean includeStartDay) {
        var sum = 0;
        for (DatePeriod period: periods) {
            sum += ChronoUnit.DAYS.between(period.getStart(), period.getEnd());
            if (includeStartDay)
                sum++;
        }

        return sum;
    }

    public static int countTotalHours(List<TimeEntry> timeEntries) {
        var sum = 0;
        for (TimeEntry timeEntry: timeEntries) {
            sum += ChronoUnit.MINUTES.between(timeEntry.getStartTime(), timeEntry.getEndTime());
        }

        return sum/60;
    }

    public static boolean isOverlapping(TimeEntry newEntry, List<TimeEntry> existingEntries) {
        boolean checkOverlap = false;
        for (TimeEntry timeEntry : existingEntries) {
            if (newEntry.getStartTime().isBefore(timeEntry.getEndTime()) && newEntry.getStartTime().isAfter(timeEntry.getStartTime())) checkOverlap = true;
            if (newEntry.getEndTime().isAfter(timeEntry.getStartTime()) && newEntry.getEndTime().isBefore(timeEntry.getEndTime())) checkOverlap = true;
            if (newEntry.getStartTime().compareTo(timeEntry.getStartTime()) == 0) checkOverlap = true;
            if (newEntry.getEndTime().compareTo(timeEntry.getEndTime()) == 0) checkOverlap = true;
        }
        return checkOverlap;
    }
}
