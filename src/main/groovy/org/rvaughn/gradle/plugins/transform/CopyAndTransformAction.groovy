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

import org.gradle.api.internal.file.CopyActionProcessingStreamAction
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.CopyActionProcessingStream
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.api.internal.file.copy.CopySpecResolver
import org.gradle.api.internal.file.copy.DefaultFileCopyDetails
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.internal.tasks.SimpleWorkResult
import org.gradle.api.tasks.WorkResult

import java.lang.reflect.Field

public class CopyAndTransformAction implements CopyAction {

    private final FileResolver fileResolver

    public CopyAndTransformAction(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    public WorkResult execute(CopyActionProcessingStream stream) {
        FileCopyDetailsInternalAction action = new FileCopyDetailsInternalAction()
        stream.process(action)
        return new SimpleWorkResult(action.didWork)
    }

    private class FileCopyDetailsInternalAction implements CopyActionProcessingStreamAction {

        private boolean didWork

        public void processFile(FileCopyDetailsInternal details) {
            File target = fileResolver.resolve(details.getRelativePath().getPathString())
            CopySpecInternal spec = extractSpec(details)
            if (!details.isDirectory()) {
                boolean sign = lookup(spec, 'signing')
                System.out.println(details.getFile().getAbsolutePath() + " -> " + target)
                System.out.println("sign: " + (sign ? 'true' : 'false'))
            }
            boolean copied = details.copyTo(target)
            if (copied) {
                didWork = true
            }
        }

        def static lookup(def specToLookAt, String propertyName) {
            if (specToLookAt?.metaClass?.hasProperty(specToLookAt, propertyName) != null) {
                def prop = specToLookAt.metaClass.getProperty(specToLookAt, propertyName)
                if (prop instanceof MetaBeanProperty) {
                    return prop?.getProperty(specToLookAt)
                } else {
                    return prop
                }
            } else {
                return null
            }
        }

        CopySpecInternal extractSpec(FileCopyDetailsInternal fileDetails) {
            if (fileDetails instanceof DefaultFileCopyDetails) {
                def startingClass = fileDetails.getClass() // It's in there somewhere
                while( startingClass != null && startingClass != DefaultFileCopyDetails) {
                    startingClass = startingClass.superclass
                }
                Field specField = startingClass.getDeclaredField('specResolver')
                specField.setAccessible(true)
                CopySpecResolver specResolver = specField.get(fileDetails)
                Field field = DefaultCopySpec.DefaultCopySpecResolver.class.getDeclaredField('this$0')
                field.setAccessible(true)
                CopySpecInternal spec = field.get(specResolver)
                return spec
            } else {
                return null
            }
        }
    }
}
