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
import be.ge0ffrey.presentations.fasterreflection.framework.*;
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

    private Person person;
    private Company company;

    private final BeanPropertyReader lmf_personName   = new LambdaMetafactoryBeanPropertyReader(Person.class, "name");
    private final BeanPropertyReader lmf_personAge    = new LambdaMetafactoryBeanPropertyReader(Person.class, "age");
    private final BeanPropertyReader lmf_companyName  = new LambdaMetafactoryBeanPropertyReader(Company.class, "name");

    private final BeanPropertyReader refl_personName  = new ReflectionBeanPropertyReader(Person.class, "name");
    private final BeanPropertyReader refl_personAge   = new ReflectionBeanPropertyReader(Person.class, "age");
    private final BeanPropertyReader refl_companyName = new ReflectionBeanPropertyReader(Company.class, "name");

    private final BeanPropertyReader mh_personName    = new MethodHandleBeanPropertyReader(Person.class, "name");
    private final BeanPropertyReader mh_personAge     = new MethodHandleBeanPropertyReader(Person.class, "age");
    private final BeanPropertyReader mh_companyName   = new MethodHandleBeanPropertyReader(Company.class, "name");

    private final BeanPropertyReader jc_personName    = JavaCompilerBeanPropertyReaderFactory.generate(Person.class, "name");
    private final BeanPropertyReader jc_personAge     = JavaCompilerBeanPropertyReaderFactory.generate(Person.class, "age");
    private final BeanPropertyReader jc_companyName   = JavaCompilerBeanPropertyReaderFactory.generate(Company.class, "name");

    @Setup
    public void setup() {
        person = new Person("Ann", 30);
        company = new Company("Acme Corporation");
    }

    @Benchmark
    public void _000_DirectAccess(Blackhole bh) {
        bh.consume(person.getName());
        bh.consume(person.getAge());
        bh.consume(company.getName());
    }

    @Benchmark
    public void _100_Reflection(Blackhole bh) {
        bh.consume(workWith(refl_personName,  person));
        bh.consume(workWith(refl_personAge,   person));
        bh.consume(workWith(refl_companyName, company));
    }

    @Benchmark
    public void _200_MethodHandle(Blackhole bh) {
        bh.consume(workWith(mh_personName,  person));
        bh.consume(workWith(mh_personAge,   person));
        bh.consume(workWith(mh_companyName, company));
    }

    @Benchmark
    public void _300_JavaCompiler(Blackhole bh) {
        bh.consume(workWith(jc_personName,  person));
        bh.consume(workWith(jc_personAge,   person));
        bh.consume(workWith(jc_companyName, company));
    }

    @Benchmark
    public void _400_LamdbaMetafactory(Blackhole bh) {
        bh.consume(workWith(lmf_personName,  person));
        bh.consume(workWith(lmf_personAge,   person));
        bh.consume(workWith(lmf_companyName, company));
    }

    public Object workWith(BeanPropertyReader reader, Object target) {
        // Call from here, in case there are different subtypes of BeanPropertyReader.
        // If the same subtype of BeanPropertyReader comes here, chances are the
        // megamorphic call-site would be somewhere in BeanPropertyReader's executeGetter().
        return reader.executeGetter(target);
    }

}
