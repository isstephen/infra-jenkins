def jobName  = 'List-S3-Files2'
def repoUrl  = 'git@github.com:isstephen/infra-jenkins.git'  
def branchToBuild   = '*/main'
def jenkinsfilePath = 'pipelines/listS3.Jenkinsfile'

pipelineJob(jobName) {
    description('List S3 objects and total size via AssumeRole (managed by Job DSL)')
    logRotator {
        daysToKeep(30)
        numToKeep(50)
    }

    parameters {
        stringParam('BUCKET', 'my162homebucket112211', 'S3 Bucket name')
        stringParam('PREFIX', 'test/', 'Path prefix (can be blank)')
        choiceParam('AWS_REGION', ['us-east-1'], 'Region')
    }

    definition {
        cpsScm {
            lightweight(true) // Use lightweight checkout to speed up the job
            scm {
                git {
                    remote {
                        url(repoUrl)
                        credentials('github-ssh') // provate repo, uncomment if needed
                    }
                    branch(branchToBuild)
                }
            }
            scriptPath(jenkinsfilePath)       // Jenkinsfile path in repo
        }
    }

    triggers {
        // 可选：定时触发或 SCM 触发
        // scm('H/15 * * * *')
        // cron('H 2 * * *')
    }

    // 可选：设置默认 agent label、权限控制等
    properties {
        // disableConcurrentBuilds()
    }
}
