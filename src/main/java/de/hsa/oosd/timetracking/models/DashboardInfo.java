package de.hsa.oosd.timetracking.models;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class DashboardInfo {
    // Personal info
    public String fullName;

    // Organization info
    public String position;
    public String organization;

    // Current info (month)
    public int targetHours;
    public int workedHours;

    // Main info
    public int holidaysTotal;
    public int holidaysUsed;
    public int holidaysLeft;
    public String nextWorkingDay;

    // Fancy stuff
    public String quoteOfTheDay = "Eat. Sleep. Work. Repeat";
    public String toDo = "Drink coffee";
    public String note = "Don't touch the LAN cable";

    // Lookups
    public List<IdNameItem> projects;


    public DashboardInfo(String fullName) {
        this.fullName = fullName;
    }

    public DashboardInfo(String fullName, String organization, String position, int targetHours, int workedHours, int holidaysTotal, int holidaysUsed, String nextWorkingDay) {
        this.fullName = fullName;
        this.organization = organization;
        this.position = position;
        this.targetHours = targetHours;
        this.workedHours = workedHours;
        this.nextWorkingDay = nextWorkingDay;

        this.holidaysLeft = holidaysTotal - holidaysUsed;
    }
}
