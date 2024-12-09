pipeline {
    agent any
    
    tools {
        maven 'maven3'
        jdk 'jdk17'
    }
    
    environment {
        DOCKER_REGISTRY = 'crypticseeds'
        MAJOR_VERSION = 'v1.0'
        VERSION = "${MAJOR_VERSION}.${BUILD_NUMBER}"
        SONAR_SCANNER_HOME = tool 'sonar-scanner'
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', credentialsId: 'git-cred', url: 'https://github.com/crypticseeds/ecommerce-microservices.git'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('sonar') {
                        sh """
                            ${SONAR_SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.projectKey=ecommerce-microservices \
                            -Dsonar.projectName=ecommerce-microservices \
                            -Dsonar.sources=. \
                            -Dsonar.java.binaries=**/target/classes
                        """
                    }
                }
            }
        }

        stage('Trivy Filesystem Scan') {
            steps {
                sh 'trivy fs --format table -o trivy-fs-report.html .'
            }
        }
        
        stage('Build and Push Images') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred') {
                        // Build and push all services using jib
                        sh "mvn clean compile jib:build -Djib.to.tags=${VERSION},latest"
                    }
                }
            }
        }
        
        stage('Trivy Image Scans') {
            parallel {
                stage('Scan Discovery Server') {
                    steps {
                        sh """
                            trivy image --no-progress --format table \
                            -o discovery-server-scan.html \
                            ${DOCKER_REGISTRY}/ecommerce-microservices-discovery-server:${VERSION}
                        """
                    }
                }
                
                stage('Scan API Gateway') {
                    steps {
                        sh """
                            trivy image --no-progress --format table \
                            -o api-gateway-scan.html \
                            ${DOCKER_REGISTRY}/ecommerce-microservices-api-gateway:${VERSION}
                        """
                    }
                }
                
                stage('Scan Inventory Service') {
                    steps {
                        sh """
                            trivy image --no-progress --format table \
                            -o inventory-service-scan.html \
                            ${DOCKER_REGISTRY}/ecommerce-microservices-inventory-service:${VERSION}
                        """
                    }
                }
                
                stage('Scan Order Service') {
                    steps {
                        sh """
                            trivy image --no-progress --format table \
                            -o order-service-scan.html \
                            ${DOCKER_REGISTRY}/ecommerce-microservices-order-service:${VERSION}
                        """
                    }
                }
                
                stage('Scan Product Service') {
                    steps {
                        sh """
                            trivy image --no-progress --format table \
                            -o product-service-scan.html \
                            ${DOCKER_REGISTRY}/ecommerce-microservices-product-service:${VERSION}
                        """
                    }
                }
                
                stage('Scan Notification Service') {
                    steps {
                        sh """
                            trivy image --no-progress --format table \
                            -o notification-service-scan.html \
                            ${DOCKER_REGISTRY}/ecommerce-microservices-notification-service:${VERSION}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            sh '''
                # Clean up docker images to free space
                docker system prune -f
                
                # Clean only the workspace Trivy cache
                rm -rf ${WORKSPACE}/.trivycache/
            '''
            archiveArtifacts artifacts: '**/*-scan.html', allowEmptyArchive: true
            cleanWs()
        }
    }
}
