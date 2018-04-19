package com.ellirion.buildframework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReflectionHelper {

    /**
     * Invokes the function {@code name} on object instance {@code inst} using arguments {@code args}.
     * @param inst The object instance on which to invoke the method.
     * @param name The name of the method.
     * @param args The arguments to pass to the function
     * @return The result of the function, or Optional.empty() on failure
     */
    public static Optional<Object> invoke(Object inst, String name, Object... args) {
        try {
            Class<?> instanceClass = inst.getClass();

            List<Object> arguments = Arrays.asList(args);
            List<Class<?>> argumentClasses = arguments.stream()
                    .map((arg) -> arg.getClass())
                    .collect(Collectors.toList());

            Method method = instanceClass.getMethod(name, argumentClasses.toArray(new Class<?>[args.length]));
            method.setAccessible(true);
            return Optional.of(method.invoke(inst, args));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param inst The object instance on which to get the field from.
     * @param name name of the field to get.
     * @return Returns a Field object.
     */
    public static Optional<Object> getField(Object inst, String name) {
        try {
            Class<?> instanceClass = inst.getClass();

            Field field = instanceClass.getField(name);
            field.setAccessible(true);
            return Optional.of(field.get(inst));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     *
     * @param inst The object instance on which to set the field.
     * @param name name of the field to set.
     * @param value the value to update the field with.
     * @return if the field has been updated.
     */
    public static boolean setField(Object inst, String name, Object value) {
        try {
            Class<?> instanceClass = inst.getClass();

            Field field = instanceClass.getField(name);
            field.setAccessible(true);
            field.set(inst, value);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
