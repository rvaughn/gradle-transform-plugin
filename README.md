# Copy And Transform Task Plugin for Gradle

This is the beginnings of a plugin to implement a `CopyAndTransform` task in
[Gradle](http://gradle.org). This basically extends the existing `Copy` task
to allow user-defined transforms to be applied to the destination files after
copying. This is needed to support certain in-place file transforms (eg.
digital signing on Windows) without compromising incremental build capability.

This is difficult to accomplish because I want to support DSL sugar to make
this convenient for build authors, but the Gradle Copy task internals were
designed to be non-extensible. In order to get around these limitations,
I have unabashedly cribbed (and in some cases copied wholesale) a whole lot
of work from the excellent [Gradle Linux Packaging Plugin]
(https://github.com/nebula-plugins/gradle-ospackage-plugin) by Netflix. All
credit for the `CopySpec`-enhancing mechanism used here goes to the authors
of that plugin.

*This is a work in progress and is not yet expected to be usable.*

# To Do

1. Get CopySpec enhancement working with the handful of hard-coded transforms
   I need.

2. Generalize the after-copy mechanic and make specific transforms user-definable.

3. Publish it.

# Future Possibilities

* Rewrite the Gradle Copy task with extensibility in mind.

* Pull out the actual copy, making it just another user-definable action.
  This will make the task useful for processing any tree of files, but will
  affect the `from`/`into` semantics and up-to-date tracking.

* Implement copy as a DSL method so that it can be used within user-defined
  tasks.

* Implement an "eachChangedFile", that is, extend or decorate `eachFile`
  to iterate only over changed files during an incremental build.

# License

All original work herein is Copyright 2015 Roger Vaughn.

Portions of this code are taken from the [Gradle Linux Packaging Plugin]
(https://github.com/nebula-plugins/gradle-ospackage-plugin) and from
[Gradle](https://github.com/gradle/gradle), and are copyright the
original authors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
