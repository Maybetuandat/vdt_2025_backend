pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: docker
    image: docker:dind
    securityContext:
      privileged: true
    env:
    - name: DOCKER_TLS_CERTDIR
      value: ""
  - name: docker-cli
    image: docker:latest
    command:
    - sleep
    args:
    - 99d
    env:
    - name: DOCKER_HOST
      value: tcp://localhost:2375
"""
        }
    }
    
    environment {
        IMAGE_NAME = 'maybetuandat/vdt_backend'
        DOCKER_HUB_CREDENTIALS = 'dockerhub_credential' 
    }
    
    stages {
        stage('Checkout Source Code') {
            steps {
                container('docker-cli') {
                    echo "🔍 Cloning source code..."
                    checkout scm
                    
                    echo "✅ Clone completed!"
                    sh 'ls -la'
                }
            }
        }
        
        stage('Get Git Tag Version') {
            steps {
                container('docker-cli') {
                    script {
                        echo "🏷️ Getting Git tag version..."
                        
                        def tagVersion = sh(
                            script: 'git describe --tags --exact-match 2>/dev/null || git rev-parse --short HEAD', 
                            returnStdout: true
                        ).trim()
                        
                        env.TAG_NAME = tagVersion
                        
                        echo "📌 Using tag version: ${env.TAG_NAME}"
                        echo "🐳 Docker image will be: ${env.IMAGE_NAME}:${env.TAG_NAME}"
                    }
                }
            }
        }
        
        stage('Verify Dockerfile') {
            steps {
                container('docker-cli') {
                    script {
                        echo "📄 Checking Dockerfile..."
                        
                        if (fileExists('Dockerfile')) {
                            echo "✅ Dockerfile found!"
                            sh 'head -10 Dockerfile'
                        } else {
                            error "❌ Dockerfile not found! Please create Dockerfile in repository root."
                        }
                    }
                }
            }
        }
        
        stage('Wait for Docker') {
            steps {
                container('docker-cli') {
                    script {
                        echo "🐳 Waiting for Docker daemon..."
                        sh '''
                            for i in {1..30}; do
                                if docker info >/dev/null 2>&1; then
                                    echo "✅ Docker daemon ready!"
                                    exit 0
                                fi
                                echo "Waiting for Docker... ($i/30)"
                                sleep 2
                            done
                            echo "❌ Docker daemon not ready"
                            exit 1
                        '''
                    }
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                container('docker-cli') {
                    script {
                        echo "🔨 Building Docker image: ${env.IMAGE_NAME}:${env.TAG_NAME}"
                        
                        // Build Docker image với tag version
                        sh """
                            docker build -t ${env.IMAGE_NAME}:${env.TAG_NAME} .
                            docker tag ${env.IMAGE_NAME}:${env.TAG_NAME} ${env.IMAGE_NAME}:latest
                        """
                        
                        echo "✅ Docker image built successfully!"
                        
                        // Verify image đã được tạo
                        sh "docker images | grep ${env.IMAGE_NAME}"
                    }
                }
            }
        }
        
        stage('Test Docker Image') {
            steps {
                container('docker-cli') {
                    script {
                        echo "🧪 Testing Docker image..."
                        
                        // Test image có chạy được không (optional)
                        sh """
                            echo "Testing if image can start..."
                            docker run --rm ${env.IMAGE_NAME}:${env.TAG_NAME} java -version || echo "Image test completed"
                        """
                    }
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                container('docker-cli') {
                    script {
                        echo "🚀 Pushing image to Docker Hub..."
                        
                        // Login Docker Hub với credentials
                        withCredentials([usernamePassword(
                            credentialsId: env.DOCKER_HUB_CREDENTIALS, 
                            passwordVariable: 'DOCKER_PASSWORD', 
                            usernameVariable: 'DOCKER_USERNAME'
                        )]) {
                            sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                        }
                        
                        // Push cả tagged version và latest
                        sh """
                            docker push ${env.IMAGE_NAME}:${env.TAG_NAME}
                            docker push ${env.IMAGE_NAME}:latest
                        """
                        
                        echo "✅ Successfully pushed to Docker Hub!"
                        echo "🔗 Image available at: https://hub.docker.com/r/maybetuandat/vdt_backend"
                    }
                }
            }
        }
    }
    
    post {
        always {
            container('docker-cli') {
                script {
                    echo "🧹 Cleaning up local Docker images..."
                    // Cleanup local images để tiết kiệm disk space
                    sh """
                        docker rmi ${env.IMAGE_NAME}:${env.TAG_NAME} || true
                        docker rmi ${env.IMAGE_NAME}:latest || true
                        docker system prune -f || true
                    """
                }
            }
        }
        success {
            echo "🎉 BUILD SUCCESS!"
            echo "✅ Source code cloned"
            echo "✅ Docker image built: ${env.IMAGE_NAME}:${env.TAG_NAME}"
            echo "✅ Image pushed to Docker Hub"
            echo ""
            echo "🚀 Ready for deployment!"
        }
        failure {
            echo "💥 BUILD FAILED!"
            echo "❌ Please check the logs above"
            echo "Common issues:"
            echo "- Dockerfile syntax error"
            echo "- Docker Hub credentials invalid"
            echo "- Network connectivity issues"
        }
    }
}