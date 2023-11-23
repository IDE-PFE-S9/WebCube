package fr.eseo.webcube.api.security;

import java.util.Date;
import java.util.Set;

import org.springframework.stereotype.Component;

import fr.eseo.webcube.api.model.Role;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class UserTokenAzure {

    private int id;

    private String given_name;

    private String typePerson;

    private String family_name;

    private Set<Role> role;

    private String mode;

    private Date expirationToken;

    private String uniqueName;

    public UserTokenAzure(){
    }

    public UserTokenAzure(int id, String typePerson, Set<Role> role){
        this.id = id;
        this.typePerson = typePerson;
        this.role = role;
    }

    public UserTokenAzure(int id, String typePerson, Set<Role> role, String mode){
        this.id = id;
        this.typePerson = typePerson;
        this.role = role;
        this.mode = mode;
    }

    public UserTokenAzure(int id, String typePerson){
        this.id = id;
        this.typePerson = typePerson;
    }

    public UserTokenAzure(String given_name, String family_name){
        this.given_name = given_name;
        this.family_name = family_name;
    }

    @Override
    public String toString() {
        return "UserDetailsResponse [id=" + id + ", given_name=" + given_name + ", typePerson=" + typePerson
                + ", family_name=" + family_name + "]";
    }
}
