package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.dom4j.tree.AbstractEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Project extends AbstractEntity {
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
    @Getter
    @Setter
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    private Organization organization;

    public Project(String name){
        this.name = name;
    }

    public Project(String name, Organization organization) {
        this.name = name;
        this.organization = organization;
    }

    @Override
    public String toString() {
        return name;
    }
}

