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

package be.ge0ffrey.presentations.fasterreflection.client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import be.ge0ffrey.presentations.fasterreflection.client.model.Person;
import be.ge0ffrey.presentations.fasterreflection.framework.BeanPropertyReader;
import be.ge0ffrey.presentations.fasterreflection.framework.MethodHandleBeanPropertyReader;
import be.ge0ffrey.presentations.fasterreflection.framework.ReflectionBeanPropertyReader;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class FasterReflectionClientBenchmark {

    private Person person;

    private final BeanPropertyReader reflectionBeanPropertyReader = new ReflectionBeanPropertyReader(Person.class, "name");
    private final BeanPropertyReader methodHandleBeanPropertyReader = new MethodHandleBeanPropertyReader(Person.class, "name");
    private static final BeanPropertyReader staticMethodHandleBeanPropertyReader = new MethodHandleBeanPropertyReader(Person.class, "name");

    @Setup
    public void setup() {
        person = new Person("Ann");
    }

    @Benchmark
    public String getNamePlain() {
        return person.getName();
    }

    @Benchmark
    public String getNameReflection() {
        return (String) reflectionBeanPropertyReader.executeGetter(person);
    }

    @Benchmark
    public String getNameMethodHandle() {
        return (String) methodHandleBeanPropertyReader.executeGetter(person);
    }

    @Benchmark
    public String getNameStaticMethodHandle() {
        return (String) staticMethodHandleBeanPropertyReader.executeGetter(person);
    }

}
