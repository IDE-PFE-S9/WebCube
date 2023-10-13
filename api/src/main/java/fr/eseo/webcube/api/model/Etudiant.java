package fr.eseo.webcube.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;


@Entity
@Getter
@Setter
public class Etudiant implements Serializable, UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEtudiant")
    private Integer idEtudiant;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "groupe")
    private String groupe;

    @Column(name = "isRattrapage")
    private boolean isRattrage;

    @Column(name = "mail")
    private String mail;

    @Column(name = "mode")
    private String mode;

    @Column(name = "path")
    private String path;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton( new SimpleGrantedAuthority( "ROLE_STUDENT" ) );
        
    }

    @Override
    public String getPassword() {
              throw new UnsupportedOperationException("Unimplemented method 'getPassword'");  

    }

    @Override
    public String getUsername() {
      throw new UnsupportedOperationException("Unimplemented method 'getUsername'");  
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;    
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
