package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.dom4j.tree.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Organization extends AbstractEntity {
    @GeneratedValue
    @Column(nullable = false)
    @Id
    @Getter
    private Long id;
    @Getter
    @Setter
    @NonNull
    @Column(unique=true)
    private String name;

    public Organization(String name){
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
