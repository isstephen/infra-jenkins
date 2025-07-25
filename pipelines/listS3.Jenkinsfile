pipeline {
  agent any
  stages {
    stage('Checkout DSL repo') {
      steps {
        git branch: 'main',
            url: 'git@github.com:isstephen/infra-jenkins.git',
            credentialsId: 'github-ssh'
      }
    }
    stage('Generate jobs') {
      steps {
        jobDsl(
          targets: 'dsl/**/*.groovy',
          removedJobAction: 'DELETE',
          removedViewAction: 'DELETE',
          lookupStrategy: 'SEED_JOB',
          sandbox: true
        )
      }
    }
  }
}
