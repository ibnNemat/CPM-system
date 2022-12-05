package uz.devops.intern.service.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String parseToStringFromLocalDate(LocalDate localDate){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return localDate.format(formatter);
        }catch (Exception e){
            return null;
        }
    }
}
