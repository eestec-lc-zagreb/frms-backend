package hr.eestec_zg.frmsbackend.config;

import hr.eestec_zg.frmsbackend.config.security.CustomUserDetailsService;
import hr.eestec_zg.frmsbackend.controllers.StatusController;
import hr.eestec_zg.frmscore.config.CoreConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackageClasses = {AppConfig.class, CoreConfig.class, StatusController.class,
        CustomUserDetailsService.class})
@PropertySource(name = "application-props", value = "${APP_PROPS:classpath:application.properties}",
        ignoreResourceNotFound = true)
public class AppConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
