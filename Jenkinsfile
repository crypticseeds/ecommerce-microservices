pipeline {
    agent any
    
    tools {
        maven 'maven3'
        jdk 'jdk17'
        dockerTool 'docker'
    }
    
    environment {
        DOCKER_REGISTRY = 'crypticseeds/ecommerce-microservices'
        VERSION = "${BUILD_NUMBER}"
        SONAR_SCANNER_HOME = tool 'sonar-scanner'
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', credentialsId: 'git-cred', url: 'my-urls.git'
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
                            -Dsonar.sources=.
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
        
        stage('Build and Test Services') {
            parallel {
                stage('Build Discovery Server') {
                    steps {
                        dir('discovery-server') {
                            sh 'mvn clean package -DskipTests'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    sh "docker build -t ${DOCKER_REGISTRY}/discovery-server:${VERSION} ."
                                }
                            }
                        }
                    }
                }
                
                stage('Build API Gateway') {
                    steps {
                        dir('api-gateway') {
                            sh 'mvn clean package -DskipTests'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    sh "docker build -t ${DOCKER_REGISTRY}/api-gateway:${VERSION} ."
                                }
                            }
                        }
                    }
                }
                
                stage('Build Inventory Service') {
                    steps {
                        dir('inventory-service') {
                            sh 'mvn clean install'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    sh "docker build -t ${DOCKER_REGISTRY}/inventory-service:${VERSION} ."
                                }
                            }
                        }
                    }
                }
                
                stage('Build Order Service') {
                    steps {
                        dir('order-service') {
                            sh 'mvn clean install'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    sh "docker build -t ${DOCKER_REGISTRY}/order-service:${VERSION} ."
                                }
                            }
                        }
                    }
                }
                
                stage('Build Product Service') {
                    steps {
                        dir('product-service') {
                            sh 'mvn clean install'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    sh "docker build -t ${DOCKER_REGISTRY}/product-service:${VERSION} ."
                                }
                            }
                        }
                    }
                }
                
                stage('Build Notification Service') {
                    steps {
                        dir('notification-service') {
                            sh 'mvn clean package -DskipTests'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    sh "docker build -t ${DOCKER_REGISTRY}/notification-service:${VERSION} ."
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Trivy Image Scans') {
            parallel {
                stage('Scan Discovery Server') {
                    steps {
                        sh "trivy image --format table --scanners vuln -o discovery-server-scan.html ${DOCKER_REGISTRY}/discovery-server:${VERSION}"
                    }
                }
                stage('Scan API Gateway') {
                    steps {
                        sh "trivy image --format table --scanners vuln -o api-gateway-scan.html ${DOCKER_REGISTRY}/api-gateway:${VERSION}"
                    }
                }
                stage('Scan Inventory Service') {
                    steps {
                        sh "trivy image --format table --scanners vuln -o inventory-service-scan.html ${DOCKER_REGISTRY}/inventory-service:${VERSION}"
                    }
                }
                stage('Scan Order Service') {
                    steps {
                        sh "trivy image --format table --scanners vuln -o order-service-scan.html ${DOCKER_REGISTRY}/order-service:${VERSION}"
                    }
                }
                stage('Scan Product Service') {
                    steps {
                        sh "trivy image --format table --scanners vuln -o product-service-scan.html ${DOCKER_REGISTRY}/product-service:${VERSION}"
                    }
                }
                stage('Scan Notification Service') {
                    steps {
                        sh "trivy image --format table --scanners vuln -o notification-service-scan.html ${DOCKER_REGISTRY}/notification-service:${VERSION}"
                    }
                }
            }
        }
        
        stage('Push Images') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred') {
                        sh "docker push ${DOCKER_REGISTRY}/discovery-server:${VERSION}"
                        sh "docker push ${DOCKER_REGISTRY}/api-gateway:${VERSION}"
                        sh "docker push ${DOCKER_REGISTRY}/inventory-service:${VERSION}"
                        sh "docker push ${DOCKER_REGISTRY}/order-service:${VERSION}"
                        sh "docker push ${DOCKER_REGISTRY}/product-service:${VERSION}"
                        sh "docker push ${DOCKER_REGISTRY}/notification-service:${VERSION}"
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/*-scan.html', allowEmptyArchive: true
            cleanWs()
        }
    }
}
