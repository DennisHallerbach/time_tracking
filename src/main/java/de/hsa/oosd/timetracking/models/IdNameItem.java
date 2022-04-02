package de.hsa.oosd.timetracking.models;

import lombok.Getter;

public class IdNameItem {
    @Getter
    private final Long id;
    @Getter
    private final String name;

    public IdNameItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
