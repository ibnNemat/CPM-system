package uz.devops.intern.service.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class ContextHolderUtil {
    public static String getUsernameFromContextHolder(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || user.getUsername() == null){
            return null;
        }
        return user.getUsername();
    }
}
