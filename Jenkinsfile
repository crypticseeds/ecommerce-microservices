pipeline {
    agent any
    
    tools {
        maven 'maven3'
        jdk 'jdk17'
        dockerTool 'docker'
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
        
        stage('Build and Test Services') {
            parallel {
                stage('Build Discovery Server') {
                    steps {
                        dir('discovery-server') {
                            sh 'mvn clean package -DskipTests'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    def imageName = "${DOCKER_REGISTRY}/ecommerce-microservices-discovery-server"
                                    sh """
                                        docker build -t ${imageName}:${VERSION} .
                                        docker tag ${imageName}:${VERSION} ${imageName}:latest
                                    """
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
                                    def imageName = "${DOCKER_REGISTRY}/ecommerce-microservices-api-gateway"
                                    sh """
                                        docker build -t ${imageName}:${VERSION} .
                                        docker tag ${imageName}:${VERSION} ${imageName}:latest
                                    """
                                }
                            }
                        }
                    }
                }
                
                stage('Build Inventory Service') {
                    steps {
                        dir('inventory-service') {
                            sh 'mvn clean package -DskipTests'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    def imageName = "${DOCKER_REGISTRY}/ecommerce-microservices-inventory-service"
                                    sh """
                                        docker build -t ${imageName}:${VERSION} .
                                        docker tag ${imageName}:${VERSION} ${imageName}:latest
                                    """
                                }
                            }
                        }
                    }
                }
                
                stage('Build Order Service') {
                    steps {
                        dir('order-service') {
                            sh 'mvn clean package -DskipTests'
                            script {
                                withDockerRegistry(credentialsId: 'docker-cred') {
                                    def imageName = "${DOCKER_REGISTRY}/ecommerce-microservices-order-service"
                                    sh """
                                        docker build -t ${imageName}:${VERSION} .
                                        docker tag ${imageName}:${VERSION} ${imageName}:latest
                                    """
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
                                    def imageName = "${DOCKER_REGISTRY}/ecommerce-microservices-product-service"
                                    sh """
                                        docker build -t ${imageName}:${VERSION} .
                                        docker tag ${imageName}:${VERSION} ${imageName}:latest
                                    """
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
                                    def imageName = "${DOCKER_REGISTRY}/ecommerce-microservices-notification-service"
                                    sh """
                                        docker build -t ${imageName}:${VERSION} .
                                        docker tag ${imageName}:${VERSION} ${imageName}:latest
                                    """
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
        
        stage('Push Images') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred') {
                        // Discovery Server
                        def discoveryServer = "${DOCKER_REGISTRY}/ecommerce-microservices-discovery-server"
                        sh """
                            docker push ${discoveryServer}:${VERSION}
                            docker push ${discoveryServer}:latest
                        """

                        // API Gateway
                        def apiGateway = "${DOCKER_REGISTRY}/ecommerce-microservices-api-gateway"
                        sh """
                            docker push ${apiGateway}:${VERSION}
                            docker push ${apiGateway}:latest
                        """

                        // Inventory Service
                        def inventoryService = "${DOCKER_REGISTRY}/ecommerce-microservices-inventory-service"
                        sh """
                            docker push ${inventoryService}:${VERSION}
                            docker push ${inventoryService}:latest
                        """

                        // Order Service
                        def orderService = "${DOCKER_REGISTRY}/ecommerce-microservices-order-service"
                        sh """
                            docker push ${orderService}:${VERSION}
                            docker push ${orderService}:latest
                        """

                        // Product Service
                        def productService = "${DOCKER_REGISTRY}/ecommerce-microservices-product-service"
                        sh """
                            docker push ${productService}:${VERSION}
                            docker push ${productService}:latest
                        """

                        // Notification Service
                        def notificationService = "${DOCKER_REGISTRY}/ecommerce-microservices-notification-service"
                        sh """
                            docker push ${notificationService}:${VERSION}
                            docker push ${notificationService}:latest
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
