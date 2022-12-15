package uz.devops.intern.command.commandImpl;

import org.reflections.Reflections;
import uz.devops.intern.command.ExecuteCommand;

import java.lang.annotation.Annotation;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(ExecuteCommand.class.getPackage().getName());
        Set<Class<? extends ExecuteCommand>> classSet = reflections.getSubTypesOf(ExecuteCommand.class);

        for (Class<?> aClass : classSet) {
            Object ob = aClass.newInstance();
//            aClass.newInstance();
            System.out.println("some class" + aClass.getName());
        }
    }
}
