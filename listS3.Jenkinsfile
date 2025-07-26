pipeline {
  agent any
  stages {
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
