// --- adhoc lambda job (SCM pipeline) ---
def jobName         = 'Adhoc-Lambda-Test'
def repoUrl         = 'git@github.com:isstephen/infra-jenkins.git'
def branchToBuild   = '*/main'
// match your repo's exact folder case:
def jenkinsfilePath = 'INFRA-JENKINS/pipelines/test_adhoc_lambda.Jenkinsfile'

pipelineJob(jobName) {
    description('Invoke adhoc-lambda with an EventBridge-style test event (managed by Job DSL)')
    logRotator {
        daysToKeep(30)
        numToKeep(50)
    }

    // Parameters are defined inside the Jenkinsfile itself, so none here.

    definition {
        cpsScm {
            lightweight(false) // nested path => do a full checkout (no @script)
            scm {
                git {
                    remote {
                        url(repoUrl)
                        credentials('github-ssh')
                    }
                    branch(branchToBuild)
                }
            }
            scriptPath(jenkinsfilePath) // Jenkinsfile path in repo (case-sensitive)
        }
    }

    // If you want a schedule, use properties/pipelineTriggers (no deprecation warnings):
    properties {
        // pipelineTriggers([cron('H 2 * * *')])  // run daily ~2am
        // pipelineTriggers([pollSCM('H/15 * * * *')])
    }
}
