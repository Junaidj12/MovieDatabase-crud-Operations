package com.mvc.crud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mvc.crud.Interceptor.AutoLoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    AutoLoginInterceptor autoLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autoLoginInterceptor);
    }
}

