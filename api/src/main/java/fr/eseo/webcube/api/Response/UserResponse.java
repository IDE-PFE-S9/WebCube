package fr.eseo.webcube.api.Response;

import java.util.Set;

import fr.eseo.webcube.api.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String uniqueName;
    private String firstname;
    private String surname;
    private Set<Role> roles;

}
