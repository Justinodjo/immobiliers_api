package com.immoservice.immobiliers.config;

import org.reflections.Reflections;

import jakarta.persistence.Entity;

import java.util.Set;

public class ReflectionsUtil {

    public static Set<Class<?>> getClassesInPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(Entity.class);
    }
}
