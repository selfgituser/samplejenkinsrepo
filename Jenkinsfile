pipeline {
    agent any

    environment {
        IMAGE_NAME = 'samplemicro'
        IMAGE_TAG = 'latest'
        AWS_ACC_ID = '982534379483.dkr.ecr.us-east-2.amazonaws.com'
        AWS_DEFAULT_REGION = 'us-east-2'
        AWS_CLUSTER='ecommerce-cluster'
        AWS_SERVICE=''

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
                          def imageName = sh(
                              script: "mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout",
                              returnStdout: true
                          ).trim()

                          def imageTag = sh(
                              script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                              returnStdout: true
                          ).trim()

                          echo "Docker Image: ${imageName}:${imageTag}"
                      }
                  }
      }



        /* stage('Build Docker Image') {
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
                                docker build -t ${AWS_ACC_ID}/${IMAGE_NAME}:${IMAGE_TAG} .
                                aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACC_ID}
                                docker push ${AWS_ACC_ID}/${IMAGE_NAME}:${IMAGE_TAG}
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
                             aws ecs register-task-definition --cli-input-json file://aws/task-definition.json
                             aws ecs update-service --cluster ${AWS_CLUSTER} --service samplejekinstask-service --task-definition samplejekinstask:3
                            '''
                         }

                    }
                } */

    }


     /* post {
        success {
            echo "Build and Docker image creation successful: ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "Build failed!"
        }
    } */
}
