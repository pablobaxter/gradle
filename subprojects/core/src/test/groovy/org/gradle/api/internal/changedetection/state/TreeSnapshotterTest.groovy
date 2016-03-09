/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.internal.changedetection.state

import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileTreeInternal
import org.gradle.api.internal.file.TestFiles
import org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory
import org.gradle.api.internal.file.collections.DefaultFileCollectionResolveContext
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.gradle.util.UsesNativeServices
import org.junit.Rule
import spock.lang.Specification

@UsesNativeServices
class TreeSnapshotterTest extends Specification {
    @Rule
    public final TestNameTestDirectoryProvider testDir = new TestNameTestDirectoryProvider();

    def "should return list of file details and cache it once"() {
        given:
        TreeSnapshotter treeSnapshotter = new TreeSnapshotter()
        def files = [testDir.createFile("a/file1.txt"),
                     testDir.createFile("a/b/file2.txt"),
                     testDir.createFile("a/b/c/file3.txt")]
        List<FileTreeInternal> fileTrees = resolveAsFileTrees()

        when:
        def fileDetails = treeSnapshotter.visitTreeForSnapshotting(fileTrees[0], true)

        then:
        fileDetails.size() == 6
        fileDetails.count { it.isDirectory() } == 3
        fileDetails.count { !it.isDirectory() } == 3
        treeSnapshotter.cachedTrees.size() == 1

        when:
        def fileDetails2 = treeSnapshotter.visitTreeForSnapshotting(fileTrees[0], true)

        then:
        fileDetails2 == fileDetails
        treeSnapshotter.cachedTrees.size() == 1
    }

    def "should not cache list of file details when there is a pattern"() {
        given:
        TreeSnapshotter treeSnapshotter = new TreeSnapshotter()
        def files = [testDir.createFile("a/file1.txt"),
                     testDir.createFile("a/b/file2.txt"),
                     testDir.createFile("a/b/c/file3.txt"),
                     testDir.createFile("a/b/c/file4.md"),
                     testDir.createFile("a/file5.md"),]
        List<FileTreeInternal> fileTrees = resolveAsFileTrees("**/*.txt")

        when:
        def fileDetails = treeSnapshotter.visitTreeForSnapshotting(fileTrees[0], true)

        then:
        fileDetails.size() == 6
        fileDetails.count { it.isDirectory() } == 3
        fileDetails.count { !it.isDirectory() } == 3
        treeSnapshotter.cachedTrees.size() == 0

        when:
        def fileDetails2 = treeSnapshotter.visitTreeForSnapshotting(fileTrees[0], true)

        then:
        !fileDetails2.is(fileDetails)
        fileDetails2.collect { it.file } as Set == fileDetails.collect { it.file } as Set
        treeSnapshotter.cachedTrees.size() == 0
    }


    private List<FileTreeInternal> resolveAsFileTrees(includePattern = null) {
        def fileResolver = TestFiles.resolver()

        def directorySet = new DefaultSourceDirectorySet("files", fileResolver, new DefaultDirectoryFileTreeFactory())
        directorySet.srcDir(testDir.getTestDirectory())
        if (includePattern) {
            directorySet.filter.include(includePattern)
        }
        DefaultFileCollectionResolveContext context = new DefaultFileCollectionResolveContext(fileResolver);
        context.add(directorySet);
        List<FileTreeInternal> fileTrees = context.resolveAsFileTrees();
        fileTrees
    }
}
