pipeline {
    agent any

    environment {
        IMAGE_NAME = ''
        IMAGE_TAG = ''
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
        stage('Extract POM Info') {
            steps {
                script {
                    // Get version (resolves inherited version from parent)
                    env.IMAGE_TAG = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout | grep -Ev '(^\\[|Download|WARNING)' | tail -n 1",
                        returnStdout: true
                    ).trim()

                    // Try to get project.name, fallback to artifactId
                    def name = sh(
                        script: "mvn help:evaluate -Dexpression=project.name -q -DforceStdout | grep -Ev '(^\\[|Download|WARNING)' | tail -n 1",
                        returnStdout: true
                    ).trim()

                    if (!name) {
                        name = sh(
                            script: "mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout | grep -Ev '(^\\[|Download|WARNING)' | tail -n 1",
                            returnStdout: true
                        ).trim()
                    }

                    env.IMAGE_NAME = name

                    echo "Resolved Project Name (IMAGE_NAME): ${env.IMAGE_NAME}"
                    echo "Resolved Project Version (IMAGE_TAG): ${env.IMAGE_TAG}"
                }
            }
        }

        /* stage('AWS') {
                    agent {
                        docker {
                            image 'amazon/aws-cli'
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
                }
 */


        stage('Build Docker Image') {

          steps {
                script {
                    sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
                }
            }
        }

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
            echo "Build and Docker image creation successful: ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
        }
        failure {
            echo "Build failed!"
        }
    }
}
