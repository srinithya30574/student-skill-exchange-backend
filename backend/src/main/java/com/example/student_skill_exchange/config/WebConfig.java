package com.example.student_skill_exchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location)
                            throws IOException {
                        Resource requested = location.createRelative(resourcePath);

                        // A real file (JS, CSS, image)? Send it.
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }

                        // An unknown /api address stays a normal 404
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }

                        // Anything else is a React page: send index.html
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}