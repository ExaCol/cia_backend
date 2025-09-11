package CIA.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
    // Cuando tenga un frontend
    /**
     * @Bean
     *       public WebMvcConfigurer corsConfigurer() {
     *       return new WebMvcConfigurer() {
     * @Override
     *           public void addCorsMappings(CorsRegistry registry) {
     *           registry.addMapping("/**")
     *           .allowedOrigins(
     *           "http://localhost:3000", // Desarrollo local
     *           "https://frontend.wagglo.com" // Producción real
     *           )
     *           .allowedMethods("GET", "POST", "PUT", "DELETE")
     *           .allowedHeaders("Authorization", "Content-Type")
     *           .allowCredentials(true);
     *           }
     *           };
     *           }
     */
}
