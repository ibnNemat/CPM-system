package uz.devops.intern.web.rest.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.devops.intern.domain.User;

import java.util.Set;
import java.util.stream.Collectors;

public class WebUtils {

    public static void setUserToContextHolder(User user){
        Set<GrantedAuthority> authorities =
            user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName())).collect(Collectors.toSet());

        org.springframework.security.core.userdetails.User principal =
            new org.springframework.security.core.userdetails.User(user.getLogin(),
                user.getPassword() == null? "": user.getPassword(), authorities);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
