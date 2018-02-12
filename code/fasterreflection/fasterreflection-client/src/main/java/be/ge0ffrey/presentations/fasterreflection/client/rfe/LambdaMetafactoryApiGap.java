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

package be.ge0ffrey.presentations.fasterreflection.client.rfe;

import java.io.File;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Function;

public class LambdaMetafactoryApiGap {

    // Proof 1
    private static final String JAR_FILE_PATH = System.getProperty("user.home") + "/.m2/repository/junit/junit/4.12/junit-4.12.jar";
    private static final String CLASS_TO_LOAD = "junit.framework.TestResult";
    private static final String METHOD_NAME = "errorCount";
    private static final Class<?> METHOD_RETURN_TYPE = Integer.TYPE;

    // Proof 2
//    private static final String JAR_FILE_PATH = "/home/ge0ffrey/.m2/repository/org/optaplanner/optaplanner-benchmark/7.5.0.Final/optaplanner-benchmark-7.5.0.Final.jar";
//    private static final String CLASS_TO_LOAD = "org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult";
//    private static final String METHOD_NAME = "getName";
//    private static final Class<?>  METHOD_RETURN_TYPE = String.class;

    public static void main(String[] args) throws Throwable {
        try {
            LambdaMetafactoryApiGap.class.getClassLoader().loadClass(CLASS_TO_LOAD);
            System.out.println("The class (" + CLASS_TO_LOAD + ") is already on the normal classpath. Cheater.");
        } catch (ClassNotFoundException e) {
            // Ok, class is not yet on this normal classpath
        }

        URL url = new File(JAR_FILE_PATH).toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
        Class<?> loadedClass = classLoader.loadClass(CLASS_TO_LOAD);

        invokeWithMethodHandle(loadedClass);
        invokeWithLambdaMetafactory(loadedClass);
    }

    private static void invokeWithMethodHandle(Class<?> loadedClass) throws Throwable {
        System.out.println("MethodHandle");
        System.out.println("============");
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup
                // Call method signature: public int errorCount() {...}
                .findVirtual(loadedClass, METHOD_NAME, MethodType.methodType(METHOD_RETURN_TYPE))
                // Cast input and output to Object (we can't import the input class in this java source)
                .asType(MethodType.methodType(Object.class, Object.class));

        Object instance = loadedClass.newInstance();
        Object returnValue = methodHandle.invokeExact(instance);
        System.out.println("MethodHandle invoke returns: " + returnValue);
        System.out.println("  but non-static MethodHandles are very slow, so as a framework developer I prefer LambdaMetafactory.");
        System.out.println();
    }

    private static void invokeWithLambdaMetafactory(Class<?> loadedClass) throws Throwable {
        System.out.println("LambdaMetafactory");
        System.out.println("=================");
        // Notice that MethodHandles.lookup(ClassLoader) doesn't exist
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    lookup.findVirtual(loadedClass, METHOD_NAME, MethodType.methodType(METHOD_RETURN_TYPE)),
                    MethodType.methodType(METHOD_RETURN_TYPE, loadedClass));
        Function getterFunction = (Function) site.getTarget().invokeExact();

        Object instance = loadedClass.newInstance();
        // Throws java.lang.ClassNotFoundException: junit.framework.TestResult
        Object returnValue = getterFunction.apply(instance);
        System.out.println("LambdaMetafactory invoke returns: " + returnValue);
    }

}
