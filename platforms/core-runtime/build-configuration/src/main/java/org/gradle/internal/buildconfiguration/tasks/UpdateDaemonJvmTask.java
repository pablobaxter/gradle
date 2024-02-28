/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.internal.buildconfiguration.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.internal.buildconfiguration.UpdateDaemonJvmModifier;
import org.gradle.internal.jvm.inspection.JvmVendor.KnownJvmVendor;
import org.gradle.jvm.toolchain.JvmImplementation;
import org.gradle.work.DisableCachingByDefault;

import javax.inject.Inject;
import java.io.File;

/**
 * Generates or updates Daemon JVM criteria.
 */
@DisableCachingByDefault(because = "Not worth caching")
public abstract class UpdateDaemonJvmTask extends DefaultTask {

    private final UpdateDaemonJvmModifier updateDaemonJvmModifier;

    @Inject
    public UpdateDaemonJvmTask(ProjectLayout projectLayout) {
        updateDaemonJvmModifier = new UpdateDaemonJvmModifier(projectLayout.getProjectDirectory().getAsFile());
    }

    @TaskAction
    void generate() {
        updateDaemonJvmModifier.updateJvmCriteria(
            getToolchainVersion().get(),
            getToolchainVendor().isPresent() ? getToolchainVendor().get().asJvmVendor() : null,
            getToolchainImplementation().getOrNull()
        );
    }

    @OutputFile
    public File getPropertiesFile() {
        return updateDaemonJvmModifier.getPropertiesFile();
    }

    @Input
    @Optional
    @Option(option = "toolchain-version", description = "The version of the toolchain required to set up Daemon JVM")
    public abstract Property<Integer> getToolchainVersion();

    @Input
    @Optional
    @Option(option = "toolchain-vendor", description = "The vendor of the toolchain required to set up Daemon JVM")
    public abstract Property<KnownJvmVendor> getToolchainVendor();

    @Input
    @Optional
    @Option(option = "toolchain-implementation", description = "The virtual machine implementation of the toolchain required to set up Daemon JVM")
    public abstract Property<JvmImplementation> getToolchainImplementation();
}
