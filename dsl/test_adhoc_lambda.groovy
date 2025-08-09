def jobName         = 'Adhoc-Lambda-Test'
def repoUrl         = 'git@github.com:isstephen/infra-jenkins.git'
def branchToBuild   = '*/main'
def jenkinsfilePath = 'pipelines/test_adhoc_lambda.Jenkinsfile' // <-- match exact case!

pipelineJob(jobName) {
  description('Invoke adhoc-lambda with an EventBridge-style test event (managed by Job DSL)')
  logRotator { daysToKeep(30); numToKeep(50) }

  definition {
    cpsScm {
      lightweight(false) // <<< critical: NO @script checkout
      scm {
        git {
          remote { url(repoUrl); credentials('github-ssh') }
          branches(branchToBuild)
        }
      }
      scriptPath(jenkinsfilePath)
    }
  }
}
