package fr.eseo.webcube.api.request;

import java.util.Set;

import fr.eseo.webcube.api.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateRoleRequest {

    private String uniqueName;
    private Set<Role> role;

}
