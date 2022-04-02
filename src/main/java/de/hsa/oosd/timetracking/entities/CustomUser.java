package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.dom4j.tree.AbstractEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class CustomUser extends AbstractEntity {
    @GeneratedValue
    @Column(nullable = false)
    @Id
    @Getter
    private Long id;
    @Getter
    @Setter
    @NonNull
    @Column(unique=true)
    private String username;
    @Getter
    @Setter
    @NonNull
    private String password;
    @Getter
    @Setter
    @NonNull
    private String role;
    @Getter
    @NonNull
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    private Organization organization;

    public CustomUser( String username, String password, String role, Organization organization ){
        this.username = username;
        this.password = password;
        this.role = role;
        this.organization = organization;
    }
}
