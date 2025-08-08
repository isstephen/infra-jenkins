def repo = 'git@github.com:isstephen/infra-jenkins.git'  
def branch = '*/main'                                            /
def cred = 'github-ssh'                                          

folder('lambda-tools') {
  displayName('lambda-tools')
  description('Jenkins jobs for Lambda utilities')
}

pipelineJob('lambda-tools/adhoc-lambda-test') {
  description('Invoke adhoc-lambda with an EventBridge-style test event')
  definition {
    cpsScm {
      scm {
        git {
          remote {
            url(repo)
            credentials(cred)
          }
          branches(branch)
        }
      }
      scriptPath('infra-jenkins/pipelines/test_adhoc_lambda.Jenkinsfile')
    }
  }
  // 也可以在这里加 job 级参数，但我们将参数放在 Jenkinsfile 中更直观
  logRotator {
    daysToKeep(14)
    numToKeep(50)
  }
  disabled(false)
}
