package com.zhouhao.config;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyOwnPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"properties"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        ArrayList<PropertySource<?>> propertySources = new ArrayList<>();
        Properties properties = new Properties();
        PropertyResourceBundle bundle = new PropertyResourceBundle(new InputStreamReader(resource.getInputStream(), "UTF-8"));
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.setProperty(key, bundle.getString(key));
        }
        if (!properties.isEmpty()) {
            propertySources.add(new PropertiesPropertySource(name, properties));
            return propertySources;
        }
        return propertySources;
    }
}