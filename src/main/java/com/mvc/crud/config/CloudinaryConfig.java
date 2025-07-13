package com.mvc.crud.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "de3e59cl5",
            "api_key", "779853376194183",
            "api_secret", "zrXnvu3TgZWacBUG-3kOs3xgRBk"
        ));
    }
}
