/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2019 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.bintray.BintrayPlugin
import org.kordamp.gradle.plugin.project.ProjectPlugin

/**
 * @author Andres Almiray
 */
class KordampParentPomPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply(ProjectPlugin)
        project.plugins.apply(BintrayPlugin)

        if (!project.hasProperty('bintrayUsername'))  project.ext.bintrayUsername  = '**undefined**'
        if (!project.hasProperty('bintrayApiKey'))    project.ext.bintrayApiKey    = '**undefined**'
        if (!project.hasProperty('sonatypeUsername')) project.ext.sonatypeUsername = '**undefined**'
        if (!project.hasProperty('sonatypePassword')) project.ext.sonatypePassword = '**undefined**'

        project.extensions.findByType(ProjectConfigurationExtension).with {
            release = (project.rootProject.findProperty('release') ?: false).toBoolean()

            info {
                vendor = 'Kordamp'

                links {
                    website      = "https://github.com/aalmiray/${project.rootProject.name}"
                    issueTracker = "https://github.com/aalmiray/${project.rootProject.name}/issues"
                    scm          = "https://github.com/aalmiray/${project.rootProject.name}.git"
                }

                people {
                    person {
                        id    = 'aalmiray'
                        name  = 'Andres Almiray'
                        roles = ['developer', 'author']
                    }
                }

                credentials {
                    sonatype {
                        username = project.sonatypeUsername
                        password = project.sonatypePassword
                    }
                }
            }

            licensing {
                licenses {
                    license {
                        id = 'Apache-2.0'
                    }
                }
            }

            javadoc {
                excludes = ['**/*.html', 'META-INF/**']
            }

            bintray {
                enabled = true
                credentials {
                    username = project.bintrayUsername
                    password = project.bintrayApiKey
                }
                repo    = 'kordamp'
                userOrg = 'aalmiray'
                name    = project.rootProject.name
            }
        }

        project.allprojects {
            repositories {
                jcenter()
                mavenCentral()
            }

            normalization {
                runtimeClasspath {
                    ignore('/META-INF/MANIFEST.MF')
                }
            }
        }

        project.allprojects { Project p ->
            def scompat = project.findProperty('sourceCompatibility')
            def tcompat = project.findProperty('targetCompatibility')

            p.tasks.withType(JavaCompile) { JavaCompile c ->
                if (scompat) c.sourceCompatibility = scompat
                if (tcompat) c.targetCompatibility = tcompat
            }
            p.tasks.withType(GroovyCompile) { GroovyCompile c ->
                if (scompat) c.sourceCompatibility = scompat
                if (tcompat) c.targetCompatibility = tcompat
            }
        }
    }
}
