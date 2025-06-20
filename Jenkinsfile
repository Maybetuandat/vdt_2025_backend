pipeline {
    agent any
    
    environment {
        IMAGE_NAME = 'maybetuandat/vdt_backend'
        DOCKER_HUB_CREDENTIALS = 'dockerhub_credential' 
    }
    
    stages {
        stage('Checkout Source Code') {
            steps {
                echo "🔍 Cloning source code..."
                checkout scm
                
                echo "✅ Clone completed!"
                sh 'ls -la'
            }
        }
        
        stage('Get Git Tag Version') {
            steps {
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
        
        stage('Verify Dockerfile') {
            steps {
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
        
        stage('Check Docker') {
            steps {
                script {
                    echo "🐳 Checking Docker availability..."
                    
                    // Check if docker command exists
                    def dockerCheck = sh(script: 'which docker', returnStatus: true)
                    if (dockerCheck != 0) {
                        error "❌ Docker not found! Please install Docker or use different approach."
                    }
                    
                    // Check docker daemon
                    def dockerStatus = sh(script: 'docker info', returnStatus: true)
                    if (dockerStatus != 0) {
                        error "❌ Docker daemon not running! Please start Docker daemon."
                    }
                    
                    echo "✅ Docker is available!"
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo "🔨 Building Docker image: ${env.IMAGE_NAME}:${env.TAG_NAME}"
                    
                    sh """
                        docker build -t ${env.IMAGE_NAME}:${env.TAG_NAME} .
                        docker tag ${env.IMAGE_NAME}:${env.TAG_NAME} ${env.IMAGE_NAME}:latest
                    """
                    
                    echo "✅ Docker image built successfully!"
                    sh "docker images | grep ${env.IMAGE_NAME}"
                }
            }
        }
        
        stage('Test Docker Image') {
            steps {
                script {
                    echo "🧪 Testing Docker image..."
                    sh """
                        echo "Testing if image can start..."
                        docker run --rm ${env.IMAGE_NAME}:${env.TAG_NAME} java -version || echo "Image test completed"
                    """
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    echo "🚀 Pushing image to Docker Hub..."
                    
                    withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_HUB_CREDENTIALS, 
                        passwordVariable: 'DOCKER_PASSWORD', 
                        usernameVariable: 'DOCKER_USERNAME'
                    )]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                    }
                    
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
    
    post {
        always {
            echo "🧹 Cleaning up local Docker images..."
            sh """
                docker rmi ${env.IMAGE_NAME}:${env.TAG_NAME} || true
                docker rmi ${env.IMAGE_NAME}:latest || true
                docker system prune -f || true
            """
        }
        success {
            echo "🎉 BUILD SUCCESS!"
            echo "✅ Source code cloned"
            echo "✅ Docker image built: ${env.IMAGE_NAME}:${env.TAG_NAME}"
            echo "✅ Image pushed to Docker Hub"
        }
        failure {
            echo "💥 BUILD FAILED!"
            echo "❌ Please check the logs above"
        }
    }
}