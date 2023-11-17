package fr.eseo.webcube.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
public class User implements Serializable {

    @Id
    @Column(name = "uniqueName")
    private String uniqueName;

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "uniqueName"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
