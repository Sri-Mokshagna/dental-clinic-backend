package com.dentalclinic.DentalClinic.config;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
public class WebConfig {
    @Bean 
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://dental-clinic-frontend-1.onrender.com")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
