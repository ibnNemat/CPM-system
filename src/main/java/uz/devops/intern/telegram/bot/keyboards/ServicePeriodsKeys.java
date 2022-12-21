package uz.devops.intern.telegram.bot.keyboards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.service.utils.ResourceBundleUtils;

import java.util.*;

@Component
public class ServicePeriodsKeys implements CreatorKeyboards{

    private final String KEY = "bot.admin.keyboards.service.period";
    private final Logger log = LoggerFactory.getLogger(ServicePeriodsKeys.class);

    @Override
    public List<String> getTextsOfButtons(String languageCode) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);

        List<String> textsOfButtons = new ArrayList<>();
        Enumeration<String> enumeration = bundle.getKeys();
        while(enumeration.hasMoreElements()){
            String text = enumeration.nextElement();
            if(text.contains(KEY)){
                textsOfButtons.add(
                    bundle.getString(text)
                );
            }
        }

        log.info("List size: {} | List: {}", textsOfButtons.size(), textsOfButtons);
        return textsOfButtons;
    }
}
