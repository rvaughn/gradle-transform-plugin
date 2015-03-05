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
import org.gradle.api.internal.tasks.SimpleWorkResult
import org.gradle.api.tasks.WorkResult

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
            if (!details.isDirectory()) {
                System.out.println(details.getFile().getAbsolutePath() + " -> " + target)
            }
            boolean copied = details.copyTo(target)
            if (copied) {
                didWork = true
            }
        }
    }
}
