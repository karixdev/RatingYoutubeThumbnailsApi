package com.github.karixdev.ratingyoutubethumbnailsapi.template.provider;

import com.github.karixdev.ratingyoutubethumbnailsapi.template.TemplateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;

@Component("thymeleafTemplateProvider")
@RequiredArgsConstructor
public class ThymeleafTemplateProvider implements TemplateProvider {
    private final SpringTemplateEngine templateEngine;

    @Override
    public String getTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        return templateEngine.process(templateName, context);
    }
}
