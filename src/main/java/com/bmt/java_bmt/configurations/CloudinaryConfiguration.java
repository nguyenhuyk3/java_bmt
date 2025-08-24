package com.bmt.java_bmt.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryConfiguration {
    @Value("${cloudinary.cloud-name}")
    String CLOUD_NAME;

    @Value("${cloudinary.api-key}")
    String API_KEY;

    @Value("${cloudinary.api-secret}")
    String API_SECRET;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET));
    }
}
