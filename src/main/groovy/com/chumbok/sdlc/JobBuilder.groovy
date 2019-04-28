#!groovy
package com.chumbok.sdlc

@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

def jobDefsFileLocation = 'src/main/resources/job-definition.yaml'
def jobDefs = new File("${jenkins.model.Jenkins.instance.getJob('JobBuilder').workspace}/" + jobDefsFileLocation).text

def jobDefsYaml = new Yaml().load(jobDefs)

jobDefsYaml['jobDefinitions'].each { jobDef ->

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


