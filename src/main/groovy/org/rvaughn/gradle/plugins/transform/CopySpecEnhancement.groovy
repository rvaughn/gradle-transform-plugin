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

import org.apache.commons.lang3.reflect.FieldUtils

import org.gradle.api.file.CopySpec
import org.gradle.api.internal.file.copy.CopySpecWrapper

@Category(CopySpec)
class CopySpecEnhancement {

    static void appendFieldToCopySpec(CopySpec spec, String fieldName, Object value) {
        def directSpec = spec
        if (spec instanceof CopySpecWrapper) {
            def delegateField = FieldUtils.getField(CopySpecWrapper, 'delegate', true)
            directSpec = delegateField.get(spec)
        }

        System.out.println("adding $fieldName to ${directSpec}")
        directSpec.metaClass["get${fieldName.capitalize()}"] = { value }
    }

    static void signing(CopySpec spec, boolean signArg) {
        System.out.println("signing")
        appendFieldToCopySpec(spec, 'signing', signArg)
    }

    static void setSigning(CopySpec spec, boolean signArg) {
        System.out.println("setSigning")
        signing(spec, signArg)
    }
}
