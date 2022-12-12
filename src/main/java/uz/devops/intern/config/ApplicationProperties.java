package uz.devops.intern.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Cpm System.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    @Getter
    @Setter
    private ApplicationProperties.Telegram telegram = new Telegram();

    @Data
    public static class Telegram {
        private String token = "5225793240:AAEDojpbQM780zRMWIvmJXCIGeEBXWoY6RM";
    }
    // jhipster-needle-application-properties-property
    // jhipster-needle-application-properties-property-getter
    // jhipster-needle-application-properties-property-class
}
