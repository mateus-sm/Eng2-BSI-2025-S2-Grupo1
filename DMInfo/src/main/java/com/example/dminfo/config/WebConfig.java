package com.example.dminfo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)

                .addPathPatterns("/**")

                .excludePathPatterns("/login")       // A própria página de login
                .excludePathPatterns("/error")       // Páginas de erro do Spring
                .excludePathPatterns("/styles/**")   // Nossos arquivos CSS
                .excludePathPatterns("/js/**");      // Nossos arquivos JS
    }
}