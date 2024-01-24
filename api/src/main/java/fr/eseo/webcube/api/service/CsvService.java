package fr.eseo.webcube.api.service;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.eseo.webcube.api.dao.RoleRepository;
import fr.eseo.webcube.api.dao.UserRepository;
import fr.eseo.webcube.api.model.Role;
import fr.eseo.webcube.api.model.User;
import java.util.Set;


@Service
public class CsvService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void importCsv(String unique_name, String firstName, String surName, String role) {

        String[] roleArray = role.split(";");
        
        Set<Role> roles = new HashSet<>();

        for(String roleName : roleArray) {
            roleName = roleName.trim();

            Optional<Role> roleOptional = roleRepository.findByRole(roleName);

            if(!roleOptional.isPresent()){
                throw new RuntimeException("Role " + roleName + " not found");
            }
            roles.add(roleOptional.get());
        }

        unique_name = unique_name.trim();
        firstName = firstName.trim();
        surName = surName.trim();
        User user = new User();
        user.setUniqueName(unique_name);
        user.setFirstname(firstName);
        user.setSurname(surName);
        user.setRoles(roles);

        userRepository.save(user);
            
        }
    }  
