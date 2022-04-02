package de.hsa.oosd.timetracking.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@NoArgsConstructor
public class Employee {
    @GeneratedValue
    @Column(nullable = false)
    @Id
    @Getter
    private int id;

    @OneToOne
    @Getter
    @JoinColumn(name = "custom_user_id")
    private CustomUser customUser;

    @Column
    @Getter
    @Setter
    private String firstName="";

    @Column
    @Getter
    @Setter
    private String lastName="";

    @Getter
    @Setter
    @Email
    @Column(unique = true)
    private String email="";

    @Column
    @Getter
    @Setter
    private String Position="";

    public Employee(CustomUser customUser) {
        this.customUser = customUser;
    }
    
    public Employee(String lastName, String firstName, String email, CustomUser customUser) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.customUser = customUser;
    }
}
