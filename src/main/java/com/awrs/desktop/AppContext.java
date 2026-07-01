package com.awrs.desktop;

import org.springframework.context.ConfigurableApplicationContext;

public final class AppContext {

    private static ConfigurableApplicationContext context;

    private AppContext() {
    }

    public static void init(ConfigurableApplicationContext ctx) {
        context = ctx;
    }

    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }
}
