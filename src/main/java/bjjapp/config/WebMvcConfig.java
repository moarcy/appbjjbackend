package bjjapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Adiciona prefixo /api a todas as rotas REST (@RestController), exceto AuthController e SchoolController
        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class)
            .and(c -> !c.getSimpleName().equals("AuthController") && !c.getSimpleName().equals("SchoolController")));
    }
}
