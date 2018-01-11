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

package be.ge0ffrey.presentations.fasterreflection.client;

import java.util.concurrent.TimeUnit;

import be.ge0ffrey.presentations.fasterreflection.client.model.Company;
import be.ge0ffrey.presentations.fasterreflection.client.model.Person;
import be.ge0ffrey.presentations.fasterreflection.framework.BeanPropertyReader;
import be.ge0ffrey.presentations.fasterreflection.framework.LambdaMetafactoryBeanPropertyReader;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class MegamorphicFasterReflectionClientBenchmark {

    private Person person1;
    private Person person2;
    private Company company1;

    private final BeanPropertyReader personNameLambdaReader = new LambdaMetafactoryBeanPropertyReader(Person.class, "name");
    private final BeanPropertyReader personAgeLambdaReader = new LambdaMetafactoryBeanPropertyReader(Person.class, "age");
    private final BeanPropertyReader companyNameLambdaReader = new LambdaMetafactoryBeanPropertyReader(Company.class, "name");

    @Setup
    public void setup() {
        person1 = new Person("Ann", 30);
        person2 = new Person("Beth", 31);
        company1 = new Company("Acme Corporation");
    }

    @Benchmark
    public void _000_DirectAccess(Blackhole blackhole) {
        blackhole.consume(person1.getName());
        blackhole.consume(person1.getAge());
        blackhole.consume(person2.getName());
        blackhole.consume(person2.getAge());
        blackhole.consume(company1.getName());
    }

    @Benchmark
    public void _400_LamdbaMetafactory(Blackhole blackhole) {
        blackhole.consume(personNameLambdaReader.executeGetter(person1));
        blackhole.consume(personAgeLambdaReader.executeGetter(person1));
        blackhole.consume(personNameLambdaReader.executeGetter(person2));
        blackhole.consume(personAgeLambdaReader.executeGetter(person2));
        blackhole.consume(companyNameLambdaReader.executeGetter(company1));
    }

}
