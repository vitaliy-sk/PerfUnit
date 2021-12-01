package dev.techh.perfunit.utils;

import io.micronaut.context.ApplicationContext;

public class Context {

    private static ApplicationContext INSTANCE;

    public static ApplicationContext getContext() {
        return INSTANCE;
    }

    public static void setContext(ApplicationContext context) {
        Context.INSTANCE = context;
    }

}
