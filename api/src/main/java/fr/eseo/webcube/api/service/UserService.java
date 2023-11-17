package fr.eseo.webcube.api.service;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.eseo.webcube.api.dao.UserRepository;
import fr.eseo.webcube.api.model.Role;
import fr.eseo.webcube.api.model.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;


	public Set<Role> getRolesByUniqueName(String uniqueName) {
		User user = userRepository.findByUniqueName(uniqueName);

		if (user != null && user.getRoles() != null) {
			// Si l'utilisateur est trouvé et a des rôles, retournez les rôles.
			return user.getRoles();
		} else {
			// Si l'utilisateur n'est pas trouvé ou n'a pas de rôles, vous pouvez choisir
			// de lancer une exception, de retourner null ou de retourner un ensemble vide
			// selon vos besoins.
			// Dans cet exemple, je retourne un ensemble vide.
			return Collections.emptySet();
		}
	}
}
