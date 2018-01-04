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

package be.ge0ffrey.presentations.fasterreflection.framework.compiler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

public final class StringGeneratedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final StringGeneratedClassLoader classLoader;
    private final Map<URI, JavaFileObject> fileObjectMap = new HashMap<>();

    public StringGeneratedJavaFileManager(JavaFileManager fileManager, StringGeneratedClassLoader classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        JavaFileObject fileObject = fileObjectMap.get(concatUri(location, packageName, relativeName));
        if (fileObject != null) {
            return fileObject;
        }
        return super.getFileForInput(location, packageName, relativeName);
    }

    public void putFileForInput(StandardLocation location, String packageName, String relativeName,
            JavaFileObject fileObject) {
        fileObjectMap.put(concatUri(location, packageName, relativeName), fileObject);
    }

    private URI concatUri(Location location, String packageName, String relativeName) {
        String uriValue = location.getName() + '/' + packageName + '/' + relativeName;
        try {
            return new URI(uriValue);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Impossible state: the generated class (" + packageName + "." + relativeName
                    + ") has an invalid URI (" + uriValue + ").", e);
        }
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject sibling) {
        if (kind != Kind.CLASS) {
            throw new IllegalArgumentException("Unsupported kind (" + kind + ") for class (" + qualifiedName + ").");
        }
        StringGeneratedClassFileObject fileObject = new StringGeneratedClassFileObject(qualifiedName);
        classLoader.addJavaFileObject(qualifiedName, fileObject);
        return fileObject;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject fileObject) {
        if (fileObject instanceof StringGeneratedClassFileObject) {
            return fileObject.getName();
        } else {
            return super.inferBinaryName(location, fileObject);
        }
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kindSet, boolean recurse)
            throws IOException {
        List<JavaFileObject> fileObjectList = new ArrayList<>();
        if (location == StandardLocation.CLASS_PATH && kindSet.contains(Kind.CLASS)) {
            for (JavaFileObject fileObject : fileObjectMap.values()) {
                if (fileObject.getKind() == Kind.CLASS && fileObject.getName().startsWith(packageName)) {
                    fileObjectList.add(fileObject);
                }
            }
            fileObjectList.addAll(classLoader.files());
        } else if (location == StandardLocation.SOURCE_PATH && kindSet.contains(Kind.SOURCE)) {
            for (JavaFileObject fileObject : fileObjectMap.values()) {
                if (fileObject.getKind() == Kind.SOURCE && fileObject.getName().startsWith(packageName)) {
                    fileObjectList.add(fileObject);
                }
            }
        }
        Iterable<JavaFileObject> parentFileObjectList = super.list(location, packageName, kindSet, recurse);
        for (JavaFileObject fileObject : parentFileObjectList) {
            fileObjectList.add(fileObject);
        }
        return fileObjectList;
    }

}
