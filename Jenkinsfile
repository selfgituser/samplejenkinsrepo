def imageName = ''
def imageTag = ''

pipeline {
    agent any

    environment {
        AWS_ACC_ID = '982534379483.dkr.ecr.us-east-2.amazonaws.com'
        AWS_DEFAULT_REGION = 'us-east-2'
        AWS_CLUSTER='ecommerce-cluster'
        AWS_SERVICE='samplejekinstask-service'
        TASK_DEF_FILE='aws/task-definition.json'
    }

    tools {
        maven 'Maven_3.8.8'  // Adjust as needed
        jdk 'JDK_17'         // Adjust according to your setup
    }


    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

      stage('Extract Docker Image Info') {
                  steps {
                      script {
                          imageName = sh(
                              script: "mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout",
                              returnStdout: true
                          ).trim()

                          imageTag = sh(
                              script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                              returnStdout: true
                          ).trim()

                          echo "Docker Image: ${imageName}:${imageTag}"

                      }
                  }
      }

      stage('Change the register config'){
                 agent {
                          docker {
                                   image 'my-aws-cli'
                                   reuseNode true
                                   args "-u root -v /var/run/docker.sock:/var/run/docker.sock --entrypoint=''"
                          }
                 }
                       steps {
                           script {

                             def fullImage = "${AWS_ACC_ID}/${imageName}:${imageTag}"
                             // Modify task definition file in-place using a temp file and jq
                             sh """
                                tmpfile=\$(mktemp)
                                jq '.containerDefinitions[0].image = "${fullImage}"' ${TASK_DEF_FILE} > "\$tmpfile" && mv "\$tmpfile" ${TASK_DEF_FILE}
                                """
                             echo "Updated ECS task definition image to: ${fullImage}"
                           }
                       }

      }



          stage('Build Docker Image') {
            agent {
                    docker {
                             image 'my-aws-cli'
                             reuseNode true
                             args "-u root -v /var/run/docker.sock:/var/run/docker.sock --entrypoint=''"
                    }
            }

          steps {
                script {
                     withCredentials([usernamePassword(credentialsId: 'awscred', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                               sh '''
                                docker build -t ${AWS_ACC_ID}/${imageName}:${imageTag} .
                                aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACC_ID}
                                docker push ${AWS_ACC_ID}/${imageName}:${imageTag}
                                '''
                     }
               }
            }
        }

          stage('AWS') {
                    agent {
                        docker {
                            image 'my-aws-cli'
                            reuseNode true
                            args "--entrypoint=''"
                        }
                    }
                    steps {
                        withCredentials([usernamePassword(credentialsId: 'awscred', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                           sh '''
                             aws --version
                             REGISTER_OUTPUT=$(aws ecs register-task-definition --cli-input-json file://${TASK_DEF_FILE})
                             # Extract the task definition ARN from the output
                             TASK_DEF_ARN=$(echo $REGISTER_OUTPUT | jq -r '.taskDefinition.taskDefinitionArn')
                             aws ecs update-service --cluster ${AWS_CLUSTER} --service ${AWS_SERVICE} --task-definition ${TASK_DEF_ARN}
                            '''
                         }

                    }
                }

    }


       post {
        success {
            echo "Build and Docker image and deployment to aws is successful: ${imageName}:${imageTag}"
        }
        failure {
            echo "Build failed!"
        }
    }
}
