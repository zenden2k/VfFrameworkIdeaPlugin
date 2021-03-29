package com.zenden2k.VfFrameworkIdeaPlugin.tests;

import com.intellij.openapi.application.ApplicationManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * We need this class to invoke tests in the event thread
 */
public class MainThreadInvocationInterceptor implements InvocationInterceptor {
    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {

        AtomicReference<Throwable> throwable = new AtomicReference<>();

        ApplicationManager.getApplication().invokeAndWait(() -> {
            try {
                invocation.proceed();
            }
            catch (Throwable t) {
                throwable.set(t);
            }
        });
        Throwable t = throwable.get();
        if (t != null) {
            throw t;
        }
    }
}
