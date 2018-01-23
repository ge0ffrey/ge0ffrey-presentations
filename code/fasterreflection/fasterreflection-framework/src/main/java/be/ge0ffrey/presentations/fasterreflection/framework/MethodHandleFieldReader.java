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
import java.lang.reflect.Field;

public class MethodHandleFieldReader implements BeanPropertyReader {

    private final MethodHandle fieldMethodHandle;

    public MethodHandleFieldReader(Class<?> beanClass, String propertyName) {
        Field field;
        try {
            field = beanClass.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
        field.setAccessible(true);
        Class<?> returnType = field.getType();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            fieldMethodHandle = lookup.unreflectGetter(field)
                    .asType(MethodType.methodType(Object.class, Object.class));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object executeGetter(Object bean) {
        try {
            return fieldMethodHandle.invokeExact(bean);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

}
