/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import be.ge0ffrey.presentations.fasterreflection.framework.compiler.StringGeneratedJavaCompilerFacade;

public class JavaCompilerBeanPropertyReaderFactory {

    public static BeanPropertyReader generate(Class<?> beanClass, String propertyName) {
        // Not 100% according to Java Beans spec, contains a bug for getHTTP() IIRC
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String packageName = JavaCompilerBeanPropertyReaderFactory.class.getPackage().getName()
                + ".generated." + beanClass.getPackage().getName();
        String simpleClassName = beanClass.getSimpleName() + "$" + propertyName;
        String fullClassName = packageName + "." + simpleClassName;
        final String source = "package " + packageName + ";\n"
                + "public class " + simpleClassName + " implements " + BeanPropertyReader.class.getName() + " {\n"
                + "    public Object executeGetter(Object bean) {\n"
                + "        return ((" + beanClass.getName() + ") bean)." + getterName + "();\n"
                + "    }\n"
                + "}";
        StringGeneratedJavaCompilerFacade compilerFacade = new StringGeneratedJavaCompilerFacade(
                JavaCompilerBeanPropertyReaderFactory.class.getClassLoader());
        Class<? extends BeanPropertyReader> compiledClass = compilerFacade.compile(
                fullClassName, source, BeanPropertyReader.class);
        try {
            return compiledClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("The generated class (" + fullClassName + ") failed to instantiate.", e);
        }
    }

}
