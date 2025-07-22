package com.cryptoadz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Para servir arquivos físicos do disco, na pasta uploads/banners
        registry.addResourceHandler("/uploads/banners/**")
                .addResourceLocations("file:uploads/banners/");

        // Para servir arquivos estáticos do classpath (resources/static/icones)
        registry.addResourceHandler("/icones/**")
                .addResourceLocations("classpath:/static/icones/");
    }
}


