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

package be.ge0ffrey.presentations.fasterreflection.client.model.polymorfism;

import be.ge0ffrey.presentations.fasterreflection.client.model.Person;

public class Cat implements Animal {

    private String name;
    private Person owner;

    public Cat() {
    }

    public Cat(String name) {
        this.name = name;
    }

    public Cat(String name, Person owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

}
