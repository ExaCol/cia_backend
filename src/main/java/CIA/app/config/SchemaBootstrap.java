package CIA.app.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SchemaBootstrap {
    //Esta insanidad se ejecuta antes del DataSeeder pa evitar duplicados al encolar cursos
    @Bean
    @Order(1)
    ApplicationRunner createPartialUniqueIndex(JdbcTemplate jdbc) {
        return args -> {
            
            jdbc.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS uq_course_queue_one_per_type
                ON public.courses_data (type)
                WHERE on_queue = true
            """);
        };
    }
}
