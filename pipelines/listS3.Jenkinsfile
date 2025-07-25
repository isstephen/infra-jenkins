pipeline {
  agent any

  environment {
    ROLE_ARN = 'arn:aws:iam::732583169994:role/JenkinS3Role' 
  }

  stages {
    stage('List S3 Files') {
      steps {
        withAWS(region: params.AWS_REGION,
                role: env.ROLE_ARN,
                roleSessionName: "jenkins-${env.BUILD_NUMBER}",
                duration: 3600) {
          sh '''
            set -e
            echo "Caller → $(aws sts get-caller-identity --query Arn --output text)"
            aws s3 ls s3://${BUCKET}/${PREFIX} --recursive --human-readable --summarize
          '''
        }
      }
    }
  }
}