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

import org.gradle.api.InvalidUserDataException
import org.gradle.api.internal.ConventionMapping
import org.gradle.api.internal.IConventionAware
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.api.internal.file.copy.DestinationRootCopySpec
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.reflect.Instantiator

class CopyAndTransformTask extends AbstractCopyTask {

    CopyAndTransformTask() {
        super()

        CopySpecExtension extension = project.extensions.findByType(CopySpecExtension)
        if (extension) {
            System.out.println("got the extension")
            //getRootSpec().with(extension.delegateCopySpec)
        }
    }

    @Override
    @TaskAction
    protected void copy() {
        use(CopySpecEnhancement) {
            System.out.println("do copy")
            super.copy()
        }
    }

    protected void applyConventions() {
        ConventionMapping mapping = ((IConventionAware)this).getConventionMapping()

        // mapping.map('signing', { false })
    }

    @Override
    protected CopyAction createCopyAction() {
        File destinationDir = getDestinationDir()
        if (destinationDir == null) {
            throw new InvalidUserDataException("No copy destination directory has been specified, use 'into' to specify a target directory.")
        }
        return new CopyAndTransformAction(getFileLookup().getFileResolver(destinationDir))
    }


    @Override
    protected CopySpecInternal createRootSpec() {
        Instantiator instantiator = getInstantiator()
        FileResolver fileResolver = getFileResolver()

        return instantiator.newInstance(DestinationRootCopySpec.class, fileResolver, super.createRootSpec())
    }

    @Override
    public DestinationRootCopySpec getRootSpec() {
        return (DestinationRootCopySpec) super.getRootSpec()
    }

    /**
     * Returns the directory to copy files into.
     *
     * @return The destination dir.
     */
    @OutputDirectory
    public File getDestinationDir() {
        return getRootSpec().getDestinationDir()
    }

    /**
     * Sets the directory to copy files into. This is the same as calling {@link #into(Object)} on this task.
     *
     * @param destinationDir The destination directory. Must not be null.
     */
    public void setDestinationDir(File destinationDir) {
        into(destinationDir)
    }

    @Override
    public AbstractCopyTask from(Object sourcePath, Closure c) {
        System.out.println("enhanced from")
        use(CopySpecEnhancement) {
            getMainSpec().from(sourcePath, c)
        }
        return this
    }

    @Override
    public AbstractCopyTask into(Object destPath, Closure configureClosure) {
        use(CopySpecEnhancement) {
            getMainSpec().into(destPath, configureClosure)
        }
        return this
    }

    @Override
    public AbstractCopyTask exclude(Closure excludeSpec) {
        use(CopySpecEnhancement) {
            getMainSpec().exclude(excludeSpec)
        }
        return this
    }

    @Override
    public AbstractCopyTask filter(Closure closure) {
        use(CopySpecEnhancement) {
            getMainSpec().filter(closure)
        }
        return this
    }

    @Override
    public AbstractCopyTask rename(Closure closure) {
        use(CopySpecEnhancement) {
            getMainSpec().rename(closure)
        }
        return this
    }
}
