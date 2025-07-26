pipeline {
    agent any

    environment {
        IMAGE_NAME = 'samplemicro'
        IMAGE_TAG = 'latest'
        AWS_ACC_ID = '982534379483.dkr.ecr.us-east-2.amazonaws.com'
        AWS_REGION = 'us-east-2'

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
                                docker build -t ${AWS_ACC_ID}/${IMAGE_NAME}:${IMAGE_TAG} .
                                aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACC_ID}
                                docker push ${AWS_ACC_ID}/${IMAGE_NAME}:${IMAGE_TAG}
                                '''
                     }
               }
            }
        }

         /* stage('AWS') {
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
                             aws s3 ls
                             aws s3 cp $WORKSPACE/target/ s3://my-image-storage-bucket-2025/jars/ --recursive --exclude "*" --include "*.jar"
                            '''
                         }

                    }
                } */


        /* stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        docker logout
                    '''
                }
            }
        } */
    }


     post {
        success {
            echo "Build and Docker image creation successful: ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "Build failed!"
        }
    }
}
