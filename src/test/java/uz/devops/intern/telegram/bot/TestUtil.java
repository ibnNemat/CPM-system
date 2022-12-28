package uz.devops.intern.telegram.bot;

import uz.devops.intern.domain.User;
import uz.devops.intern.web.rest.vm.ManagedUserVM;

public class TestUtil {
    private static final String LOGIN = "sardorbroo";
    private static final String FIRST_NAME = "Sardor";
    private static final String LAST_NAME = "Shorahimov";
    private static final String EMAIL = "thesardorisfire@gmail.com";
    private static final String PHONE_NUMBER = "+998999124625";
    private static final String PASSWORD = "admin";

    public static ManagedUserVM createUser(){
        ManagedUserVM vm = new ManagedUserVM();
        vm.setFirstName(FIRST_NAME);
        vm.setLastName(LAST_NAME);
        vm.setLogin(LOGIN);
        vm.setPassword(PASSWORD);
        vm.setEmail(EMAIL);
        vm.setLangKey("uz");

        return vm;
    }
}
