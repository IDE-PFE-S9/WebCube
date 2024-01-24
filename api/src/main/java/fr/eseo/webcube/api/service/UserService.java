package fr.eseo.webcube.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eseo.webcube.api.Response.UserResponse;
import fr.eseo.webcube.api.dao.RoleRepository;
import fr.eseo.webcube.api.dao.UserRepository;
import fr.eseo.webcube.api.model.Role;
import fr.eseo.webcube.api.model.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;


	public Set<Role> getRolesByUniqueName(String uniqueName) {
		User user = userRepository.findByUniqueName(uniqueName).get();

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

  public Optional<User> getUserByUniqueName(String uniqueName) {
    return userRepository.findByUniqueName(uniqueName);
  }

	@Transactional(readOnly = true)
    public List<UserResponse> findAllWithRoles() {
		List<Object[]> users = userRepository.findAllWithRoles();
		List<UserResponse> listUserResponse = new ArrayList<>();
		for (Object[] user : users) {
			UserResponse userResponse = new UserResponse();
			userResponse.setUniqueName((String) user[0]);
			userResponse.setFirstname((String) user[1]);
			userResponse.setSurname((String) user[2]);
			Set<Role> roles = new HashSet<>();
			String[] roleIdArray = ((String) user[3]).split(",");
			String[] roleNameArray = ((String) user[4]).split(",");
			List<String> roleIdList = Arrays.asList(roleIdArray);
			List<String> roleNameList = Arrays.asList(roleNameArray);

			// Assuming roleIdList and roleNameList have the same size
			Iterator<String> roleIdIterator = roleIdList.iterator();
			Iterator<String> roleNameIterator = roleNameList.iterator();

			while (roleIdIterator.hasNext() && roleNameIterator.hasNext()) {
				Role role = new Role();
				role.setIdRole(Integer.parseInt(roleIdIterator.next()));
				role.setRole(roleNameIterator.next());
				roles.add(role);
			}
			userResponse.setRoles(roles);
			listUserResponse.add(userResponse);
   		 }
		return listUserResponse;
	}

	public void updateRoles(String uniqueName, Set<Role> newRoles) {
		Optional<User> optionalUser = userRepository.findByUniqueName(uniqueName);

		Boolean roleGroupA = false;
		Boolean roleGroupB = false;

		for (Role role : newRoles){
			if (role.getRole().equals("ROLE_GROUP_A")){
				roleGroupA = true;
			}
			if (role.getRole().equals("ROLE_GROUP_B")){
				roleGroupB = true;
			}
		}
		if(roleGroupA && roleGroupB){
			throw new IllegalArgumentException("Impossible d'ajouter les deux rôles");
		}

		if (optionalUser.isPresent()){
			Optional<Role> roleTeacher = roleRepository.findByRole("ROLE_TEACHER");
			if (newRoles.contains(roleTeacher.get()) && !optionalUser.get().getRoles().contains(roleTeacher.get())){
				throw new IllegalArgumentException("Impossible d'ajouter le rôle enseignant");
			}

			optionalUser.get().setRoles(newRoles);

			// Save the updated user
			userRepository.save(optionalUser.get());

		} else{
			throw new IllegalArgumentException("Utilisateur non trouvé");
		}
	}

	public Role getTeacherRole() {
		Optional<Role> roleTeacher = roleRepository.findByRole("ROLE_TEACHER");
		if (roleTeacher.isPresent()){
			return roleTeacher.get();
		} else{
			throw new IllegalArgumentException("Rôle enseignant non trouvé");
		}
	}

}
