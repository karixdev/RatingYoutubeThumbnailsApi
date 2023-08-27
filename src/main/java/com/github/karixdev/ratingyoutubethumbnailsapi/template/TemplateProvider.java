package com.github.karixdev.ratingyoutubethumbnailsapi.template;

import java.util.Map;

public interface TemplateProvider {
    String getTemplate(String templateName, Map<String, Object> variables);
}
