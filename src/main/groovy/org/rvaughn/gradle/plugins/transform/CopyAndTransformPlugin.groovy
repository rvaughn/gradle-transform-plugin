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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.ConventionMapping
import org.gradle.api.internal.IConventionAware

class CopyAndTransformPlugin implements Plugin<Project> {

    CopySpecExtension extension

    void apply(Project project) {
        System.out.println("*************************")
        extension = project.extensions.create('copyAndTransform', CopySpecExtension, project)
        ConventionMapping mapping = ((IConventionAware)extension).getConventionMapping()

        project.ext.CopyAndTransform = CopyAndTransformTask.class

        project.tasks.withType(CopyAndTransformTask) { CopyAndTransformTask t ->
            t.applyConventions()
        }
    }

}
