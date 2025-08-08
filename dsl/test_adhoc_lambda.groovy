def repo   = 'git@github.com:isstephen/infra-jenkins.git'
def branch = '*/main'
def cred   = 'github-ssh'

// Creates: folder + pipeline job sourced from this repo
folder('lambda-tools') {
  displayName('lambda-tools')
  description('Jenkins jobs for Lambda utilities')
}

pipelineJob('lambda-tools/adhoc-lambda-test') {
  description('Invoke adhoc-lambda with an EventBridge-style test event')
  definition {
    cpsScm {
      lightweight(false)
      scm {
        git {
          remote { url(repo); credentials(cred) }
          branches(branch)
        }
      }
      scriptPath('infra-jenkins/pipelines/test_adhoc_lambda.Jenkinsfile')
    }
  }
  logRotator { daysToKeep(14); numToKeep(50) }
  disabled(false)
}