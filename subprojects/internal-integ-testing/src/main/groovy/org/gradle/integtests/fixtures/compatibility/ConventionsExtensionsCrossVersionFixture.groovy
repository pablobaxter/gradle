/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.integtests.fixtures.compatibility

import org.gradle.api.JavaVersion
import org.gradle.util.GradleVersion

/**
 * Cross version testing fixture for accessing extensions and conventions.
 */
class ConventionsExtensionsCrossVersionFixture {

    static String javaSourceCompatibility(GradleVersion targetVersion, JavaVersion javaVersion) {
        return javaCompatibility(targetVersion, javaVersion, 'source')
    }

    static String javaTargetCompatibility(GradleVersion targetVersion, JavaVersion javaVersion) {
        return javaCompatibility(targetVersion, javaVersion, 'target')
    }

    private static String javaCompatibility(GradleVersion targetVersion, JavaVersion javaVersion, String compatibility) {
        if (targetVersion >= GradleVersion.version("5.0")) {
            return "java.${compatibility}Compatibility = JavaVersion.${javaVersion.name()}"
        } else {
            return "${compatibility}Compatibility = '${javaVersion.toString()}'"
        }
    }
}
