package fr.eseo.webcube.api.security;

/*import com.projetgleseo.api.model.Person;
import com.projetgleseo.api.model.PlanningAssistant;
import com.projetgleseo.api.model.Student;
import com.projetgleseo.api.model.Teacher;*/

import fr.eseo.webcube.api.model.Etudiant;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtUtil;

    // Cette classe permet de vérifier les JWTs reçus
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
        } else {
            String token = getAccessToken(request);
            if (!jwtUtil.validateAccessToken(token)) {
                filterChain.doFilter(request, response);
            } else {
                setAuthenticationContext(token, request);
                filterChain.doFilter(request, response);
            }
        }
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer");
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header.split(" ")[1].trim();
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // TODO recuperation des informations du token
    private UserDetails getUserDetails(String token) {
        Etudiant userDetails = new Etudiant();
        Claims claims = jwtUtil.parseClaims(token);
        String role = (String) claims.get("role");
        role = role.replace("[", "").replace("]", "");
        String[] jwtSubject = jwtUtil.getSubject(token).split(",");
        if (role.equals("ROLE_TEACHER")) {
            //userDetails = new Teacher();
        } else if (role.equals("ROLE_STUDENT")) {

            //userDetails = new Student();
        } else {
            //userDetails = new PlanningAssistant();
        }
        //userDetails.setPermission(role);
        //userDetails.setUsername(jwtSubject[0]);
        return userDetails;
    }
}
