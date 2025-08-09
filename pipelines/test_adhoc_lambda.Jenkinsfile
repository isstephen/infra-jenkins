pipeline {
  agent any

  parameters {
    string(name: 'FUNCTION', defaultValue: 'adhoc-lambda', description: 'Lambda name')
    string(name: 'REGION',   defaultValue: 'us-east-1',    description: 'AWS region')
    string(name: 'BUCKET',   defaultValue: 'src-demo-9994')
    string(name: 'PREFIX',   defaultValue: 'DW_Export/unprocessed') // 不要尾随斜杠
    choice(name: 'MODE', choices: ['ALL_IN_DIR','TABLE_ONLY'], description: 'ALL=process_cob_files.ind; TABLE=<table>.ind')
    string(name: 'DATE',  defaultValue: '', description: 'YYYYMMDD (留空=自动选择最新有数据的日期)')
    string(name: 'TABLE', defaultValue: 'employee_data', description: '仅 MODE=TABLE_ONLY 时生效')
  }

  stages {
    stage('Build test event') {
      steps {
        script {
          // 1) 解析/自动获取 DATE
          def d = params.DATE?.trim()
          if (!d) {
            // 在 PREFIX 下找到最新的日期目录（使用 shell 环境变量，避免 Groovy 解析 $）
            d = sh(returnStdout: true, script: '''
              set -e
              LATEST=$(aws s3api list-objects-v2 \
                --bucket "$BUCKET" \
                --prefix "$PREFIX/" --delimiter '/' \
                --query "reverse(sort_by(CommonPrefixes,&Prefix))[*].Prefix" \
                --output text | awk -F/ '/[0-9]{8}\/$/{print $NF; exit}')
              echo "${LATEST:-}"
            ''').trim()
            if (!d) { error "No date folders under s3://${params.BUCKET}/${params.PREFIX}/" }

            // 确认该目录里至少有一个非 .ind 文件
            def count = sh(returnStdout: true, script: '''
              aws s3api list-objects-v2 \
                --bucket "$BUCKET" \
                --prefix "$PREFIX/$DATE/" \
                --query "Contents[].Key" --output text | grep -Ev '\.ind$' | wc -l
            ''', environment: [ "DATE=${d}" ]).trim()
            if (count == '0') { error "Folder ${params.PREFIX}/${d}/ has no data files to touch." }
          }
          env.DATE = d

          // 2) 计算 key
          def key = (params.MODE == 'ALL_IN_DIR')
            ? "${params.PREFIX}/${d}/process_cob_files.ind"
            : "${params.PREFIX}/${d}/${params.TABLE}.ind"

          // 3) 生成 EventBridge 风格的测试事件
          writeFile file: 'event.json', text: """{
  "version":"0",
  "detail-type":"Object Created",
  "source":"aws.s3",
  "account":"732583169994",
  "region":"${params.REGION}",
  "detail":{
    "bucket":{"name":"${params.BUCKET}"},
    "object":{"key":"${key}","size":0}
  }
}"""
        }
        sh 'echo "Event JSON:" && cat event.json'
      }
    }

    stage('Invoke Lambda with test event') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
                          credentialsId: 'assume-JenkinS3Role']]) {
          sh '''
            echo "Caller -> $(aws sts get-caller-identity --query Arn --output text)"

            aws lambda invoke \
              --function-name "${FUNCTION}" \
              --region "${REGION}" \
              --payload fileb://event.json \
              --cli-binary-format raw-in-base64-out \
              --log-type Tail response.json \
              --query LogResult --output text | base64 -d | tee lambda.log

            TOUCH_COUNT=$(grep -c "Re-uploaded (touch)" lambda.log || true)
            echo "Touched objects: ${TOUCH_COUNT}"

            # ALL_IN_DIR 模式下必须触到文件，否则失败
            if [ "$MODE" = "ALL_IN_DIR" ] && [ "$TOUCH_COUNT" -le 0 ]; then
              echo "No files touched in ALL_IN_DIR mode" >&2
              exit 1
            fi
          '''
        }
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'event.json, response.json, lambda.log', fingerprint: true
    }
  }
}
