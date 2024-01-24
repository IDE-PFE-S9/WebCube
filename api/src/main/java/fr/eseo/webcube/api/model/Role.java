package fr.eseo.webcube.api.model;

import java.util.Set;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRole")
    private Integer idRole;
    
    @Column(name = "Role")
    private String role;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users;

    @Override
    public String toString() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(idRole, role.idRole) &&
            Objects.equals(role, role.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRole, role);
    }
}
