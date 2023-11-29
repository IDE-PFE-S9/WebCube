package fr.eseo.webcube.api.security;

import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public class CustomClaimSetConverter implements Converter<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(Map<String, Object> map) {
        return map;
    }
}

