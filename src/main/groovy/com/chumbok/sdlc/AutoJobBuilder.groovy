#!groovy
package com.chumbok.sdlc

import groovy.json.JsonSlurper

def org = 'mmahmoodictbd'
println("Fetching repositories from ")

def repos = new JsonSlurper()
        .parse(new URL("https://api.github.com/users/${org}/repos?per_page=100").newReader())

for (repo in repos) {

    println("https://raw.githubusercontent.com/${org}/${repo.name}/master/Jenkinsfile")
    jenkinsfileUrl = new URL("https://raw.githubusercontent.com/${org}/${repo.name}/master/Jenkinsfile")

    def hasJenkinsfile = false
    try {
        hasJenkinsfile = !jenkinsfileUrl.text.isEmpty()
    } catch (e) {}

    if (!hasJenkinsfile) {
        println("${repo.name} does not have Jenkinsfile.")
        continue
    }

    def jobDef = [name: repo.name, jenkinsFolder: org, gitUrl: repo.svn_url]
    println(jobDef)

    println "Creating job: ${jobDef.name} for ${jobDef.gitUrl}"

    println "Creating Project Folder: ${jobDef.jenkinsFolder}"
    folder(jobDef.jenkinsFolder) {
    }

    pipelineJob(jobDef.jenkinsFolder + "/" + jobDef.name) {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(jobDef.gitUrl + '.git')
                        }
                        branches('master', '**/feature*')
                        scriptPath('Jenkinsfile')
                    }
                }
            }
        }
    }
    println "Pipeline job: ${jobDef.jenkinsFolder}/${jobDef.name} created successfully."

    multibranchPipelineJob(jobDef.jenkinsFolder + "/" + jobDef.name + "-ci") {

        description("On SCM: ${jobDef.gitUrl}")

        triggers {
            cron('H H * * *')
        }

        branchSources {
            github {
                id("repo-${jobDef.name}")
                repoOwner(jobDef.jenkinsFolder)
                repository(jobDef.name)
                scanCredentialsId('github-ci')
            }
        }

        orphanedItemStrategy {
            discardOldItems {
                daysToKeep(1)  // remove merged pipelines every day
            }
        }
    }

    println "Multibranch Pipeline job: ${jobDef.jenkinsFolder}/${jobDef.name} created successfully."

}