package ru.airiva.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WebAppInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[0];
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("encoding-filter");
        servletContext.addFilter("encoding-filter", new CharacterEncodingFilter(UTF_8.name(), true))
                .addMappingForUrlPatterns(null, true, "/*");
        super.onStartup(servletContext);
    }
}
