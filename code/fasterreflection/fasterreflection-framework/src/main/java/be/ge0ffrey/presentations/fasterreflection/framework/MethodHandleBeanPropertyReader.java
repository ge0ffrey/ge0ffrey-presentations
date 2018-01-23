/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ge0ffrey.presentations.fasterreflection.framework;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class MethodHandleBeanPropertyReader implements BeanPropertyReader {

    private final MethodHandle getterMethodHandle;

    public MethodHandleBeanPropertyReader(Class<?> beanClass, String propertyName) {
        // Not 100% according to Java Beans spec, contains a bug for getHTTP() IIRC
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method getterMethod;
        try {
            getterMethod = beanClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class (" + beanClass + ") has doesn't have the getter method ("
                    + getterName + ").", e);
        }
        Class<?> returnType = getterMethod.getReturnType();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            getterMethodHandle = lookup.findVirtual(getterMethod.getDeclaringClass(), getterMethod.getName(), MethodType.methodType(returnType))
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException("MethodHandle creation failed for method (" + getterMethod + ").", e);
        }
    }

    public MethodHandle getGetterMethodHandle() {
        return getterMethodHandle;
    }

    public Object executeGetter(Object bean) {
        try {
            return getterMethodHandle.invokeExact(bean);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

}
