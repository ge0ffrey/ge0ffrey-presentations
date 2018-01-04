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
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String packageName = JavaCompilerBeanPropertyReaderFactory.class.getPackage().getName()
                + ".generated." + beanClass.getPackage().getName();
        String simpleClassName = beanClass.getSimpleName() + "_" + propertyName; // TODO replace _ by $
        String fullClassName = packageName + "." + simpleClassName;
        final String source = "package " + packageName + ";\n"
                + "public class " + simpleClassName + "\n"
                + "        implements " + BeanPropertyReader.class.getName() + " {\n"
                + "    public Object executeGetter(Object bean) {\n"
                + "        return ((" + beanClass.getName() + ") bean)." + getterName + "();"
                + "    }"
                + "}";
        StringGeneratedJavaCompilerFacade compilerFacade = new StringGeneratedJavaCompilerFacade(
                JavaCompilerBeanPropertyReaderFactory.class.getClassLoader());
        Class<? extends BeanPropertyReader> compiledClass = compilerFacade.compile(
                packageName, simpleClassName, source, BeanPropertyReader.class);
        try {
            return compiledClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("The generated class (" + fullClassName + ") failed to instantiate.", e);
        }


//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        if (compiler == null) {
//            throw new IllegalStateException("Cannot find the system Java compiler.\n"
//                    + "Maybe you're using the JRE without the JDK: either the classpath lacks a jar (tools.jar)"
//                    + " xor the modulepath lacks a module (java.compiler).");
//        }
//        String uriValue = simpleClassName + ".java";
//        URI uri;
//        try {
//            uri = new URI(uriValue);
//        } catch (URISyntaxException e) {
//            throw new IllegalStateException("Generated class (" + fullClassName
//                    + ") failed before compilation due to an invalid URI (" + uriValue + ").", e);
//        }
//        JavaCompiler.CompilationTask task = compiler.getTask(null, compiler.getStandardFileManager(null, null, null), null, null, null,
//                Collections.singletonList(
//                        new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
//                            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
//                                return source;
//                            }
//                        }
//                )
//        );
//        boolean success = task.call();
//        if (!success) {
//            // TODO include compile errors in exception message
//            throw new IllegalStateException("Generated class (" + fullClassName + ") failed to compile.");
//        }
//        Class<?> compiledClass;
//        try {
//            compiledClass = Class.forName(fullClassName);
//        } catch (ClassNotFoundException e) {
//            throw new IllegalStateException("Generated class (" + fullClassName + ") failed to load.", e);
//        }
//        try {
//            return (BeanPropertyReader) compiledClass.newInstance();
//        } catch (InstantiationException | IllegalAccessException e) {
//            throw new IllegalStateException("Generated class (" + fullClassName + ") failed to instantiate.", e);
//        }
    }
}
