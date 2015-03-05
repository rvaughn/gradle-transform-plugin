/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rvaughn.gradle.plugins.transform

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.*
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.api.internal.file.copy.CopySpecResolver
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.internal.reflect.Instantiator

import java.util.regex.Pattern

/**
 * Explanation from gradle-ospackage-plugin:
 *
 * An extension which can be attached to the project.
 *
 * We can't extend DefaultCopySpec, since it's @NotExtensible, meaning that we won't get any convention
 * mappings. If we extend DelegatingCopySpec we get groovy compilation errors around the return types between
 * CopySourceSpec's methods and the ones overridden in DelegatingCopySpecInternal, even though that's perfectly
 * valid Java code. The theory is that it's some bug in groovyc.
 *
 * Updated here to support the current interface of DelegatingCopySpecInternal as of 3-5-2015.
 */
public class CopySpecExtension implements CopySpecInternal {

    @Input @Optional
    Boolean signing

    CopySpecInternal delegateCopySpec;

    public CopySpecExtension(Project project) {
        FileResolver resolver = ((ProjectInternal) project).getFileResolver()
        Instantiator instantiator = ((ProjectInternal) project).getServices().get(Instantiator.class)
        delegateCopySpec = new DefaultCopySpec(resolver, instantiator)
    }

    /*
     * Special Use cases that involve Closures which we want to wrap:
     */
    CopySpec from(Object sourcePath, Closure c) {
        System.out.println("enhancing from: $sourcePath")
        use(CopySpecEnhancement) {
            return getDelegateCopySpec().from(sourcePath, c);
        }
    }

    CopySpec into(Object destPath, Closure configureClosure) {
        use(CopySpecEnhancement) {
            return getDelegateCopySpec().into(destPath, configureClosure)
        }
    }

    CopySpec include(Closure includeSpec) {
        use(CopySpecEnhancement) {
            return getDelegateCopySpec().include(includeSpec)
        }
    }

    CopySpec exclude(Closure excludeSpec) {
        use(CopySpecEnhancement) {
            return getDelegateCopySpec().exclude(excludeSpec)
        }
    }

    CopySpec filter(Closure closure) {
        use(CopySpecEnhancement) {
            return getDelegateCopySpec().filter(closure)
        }
    }

    CopySpec rename(Closure closure) {
        use(CopySpecEnhancement) {
            return getDelegateCopySpec().rename(closure)
        }
    }

    CopySpec eachFile(Closure closure) {
        use(CopySpecEnhancement) {
            return getDelegateCopySpec().eachFile(closure)
        }
    }

    /*
     * Copy and Paste from org.gradle.api.internal.file.copy.DelegatingCopySpecInternal, since extending it causes
     * compilation problems. The methods above are special cases and are omitted below.
     */
    public boolean hasSource() {
        return getDelegateCopySpec().hasSource();
    }

    public boolean isCaseSensitive() {
        return getDelegateCopySpec().isCaseSensitive();
    }

    public void setCaseSensitive(boolean caseSensitive) {
        getDelegateCopySpec().setCaseSensitive(caseSensitive);
    }

    public boolean getIncludeEmptyDirs() {
        return getDelegateCopySpec().getIncludeEmptyDirs();
    }

    public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
        getDelegateCopySpec().setIncludeEmptyDirs(includeEmptyDirs);
    }

    public DuplicatesStrategy getDuplicatesStrategy() {
        return getDelegateCopySpec().getDuplicatesStrategy();
    }

    public void setDuplicatesStrategy(DuplicatesStrategy strategy) {
        getDelegateCopySpec().setDuplicatesStrategy(strategy);
    }

    public CopySpec filesMatching(String pattern, Action<? super FileCopyDetails> action) {
        return getDelegateCopySpec().filesMatching(pattern, action);
    }

    public CopySpec filesNotMatching(String pattern, Action<? super FileCopyDetails> action) {
        return getDelegateCopySpec().filesNotMatching(pattern, action);
    }

    public CopySpec with(CopySpec... sourceSpecs) {
        return getDelegateCopySpec().with(sourceSpecs);
    }

    public CopySpec from(Object... sourcePaths) {
        return getDelegateCopySpec().from(sourcePaths);
    }

    public CopySpec setIncludes(Iterable<String> includes) {
        return getDelegateCopySpec().setIncludes(includes);
    }

    public CopySpec setExcludes(Iterable<String> excludes) {
        return getDelegateCopySpec().setExcludes(excludes);
    }

    public CopySpec include(String... includes) {
        return getDelegateCopySpec().include(includes);
    }

    public CopySpec include(Iterable<String> includes) {
        return getDelegateCopySpec().include(includes);
    }

    public CopySpec include(Spec<FileTreeElement> includeSpec) {
        return getDelegateCopySpec().include(includeSpec);
    }

    public CopySpec exclude(String... excludes) {
        return getDelegateCopySpec().exclude(excludes);
    }

    public CopySpec exclude(Iterable<String> excludes) {
        return getDelegateCopySpec().exclude(excludes);
    }

    public CopySpec exclude(Spec<FileTreeElement> excludeSpec) {
        return getDelegateCopySpec().exclude(excludeSpec);
    }

    public CopySpec into(Object destPath) {
        return getDelegateCopySpec().into(destPath);
    }

    public CopySpec rename(String sourceRegEx, String replaceWith) {
        return getDelegateCopySpec().rename(sourceRegEx, replaceWith);
    }

    public CopyProcessingSpec rename(Pattern sourceRegEx, String replaceWith) {
        return getDelegateCopySpec().rename(sourceRegEx, replaceWith);
    }

    public CopySpec filter(Map<String, ?> properties, Class<? extends FilterReader> filterType) {
        return getDelegateCopySpec().filter(properties, filterType);
    }

    public CopySpec filter(Class<? extends FilterReader> filterType) {
        return getDelegateCopySpec().filter(filterType);
    }

    public CopySpec expand(Map<String, ?> properties) {
        return getDelegateCopySpec().expand(properties);
    }

    public CopySpec eachFile(Action<? super FileCopyDetails> action) {
        return getDelegateCopySpec().eachFile(action);
    }

    public Integer getFileMode() {
        return getDelegateCopySpec().getFileMode();
    }

    public CopyProcessingSpec setFileMode(Integer mode) {
        return getDelegateCopySpec().setFileMode(mode);
    }

    public Integer getDirMode() {
        return getDelegateCopySpec().getDirMode();
    }

    public CopyProcessingSpec setDirMode(Integer mode) {
        return getDelegateCopySpec().setDirMode(mode);
    }

    public Set<String> getIncludes() {
        return getDelegateCopySpec().getIncludes();
    }

    public Set<String> getExcludes() {
        return getDelegateCopySpec().getExcludes();
    }

    public Iterable<CopySpecInternal> getChildren() {
        return getDelegateCopySpec().getChildren();
    }

    public CopySpecInternal addChild() {
        return getDelegateCopySpec().addChild();
    }

    public CopySpecInternal addChildBeforeSpec(CopySpecInternal spec) {
        return getDelegateCopySpec().addChildBeforeSpec(spec);
    }

    public CopySpecInternal addFirst() {
        return getDelegateCopySpec().addFirst();
    }

    public void walk(Action<? super CopySpecResolver> action) {
        getDelegateCopySpec().walk(action);
    }

    public CopySpecResolver buildRootResolver() {
        return getDelegateCopySpec().buildRootResolver();
    }

    public CopySpecResolver buildResolverRelativeToParent(CopySpecResolver parent) {
        return getDelegateCopySpec().buildResolverRelativeToParent(parent);
    }
}
