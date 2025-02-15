package com.budgetapi;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ClearDatabaseExtension implements AfterEachCallback {
    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        Flyway flyway = SpringExtension.getApplicationContext(extensionContext)
                .getBean(Flyway.class);
        flyway.clean();
        flyway.migrate();
    }
}
