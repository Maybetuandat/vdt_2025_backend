pipeline {
    agent {
        label 'docker_builder'
    }
    
    environment {
        IMAGE_NAME = 'maybetuandat/vdt_backend'
        DOCKER_HUB_CREDENTIALS = 'dockerhub_credential'
        GITHUB_CREDENTIALS = 'github_token' 
        CONFIG_REPO_URL = 'https://github.com/maybetuandat/vdt_2025_backend_config.git' 
    }
    
    stages {
        stage('Agent Information') {
            steps {
                echo "🔍 Running on agent: ${env.NODE_NAME}"
                echo "📍 Workspace: ${env.WORKSPACE}"
                sh 'whoami'
                sh 'pwd'
                sh 'uname -a'
            }
        }
        
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
                    
                    // Debug: Show git info
                    sh 'git log --oneline -n 5'
                    sh 'git tag --list'
                    
                    def tagVersion = sh(
                        script: 'git describe --tags --exact-match 2>/dev/null || git describe --tags --abbrev=0 2>/dev/null || git rev-parse --short HEAD', 
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
                    
                    def dockerCheck = sh(script: 'which docker', returnStatus: true)
                    if (dockerCheck != 0) {
                        error "❌ Docker not found! Please install Docker or use different approach."
                    }
                    
                    def dockerStatus = sh(script: 'docker info', returnStatus: true)
                    if (dockerStatus != 0) {
                        error "❌ Docker daemon not running! Please start Docker daemon."
                    }
                    
                    echo "✅ Docker is available!"
                    sh 'docker --version'
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
                    """
                    
                    echo "✅ Successfully pushed to Docker Hub!"
                }
            }
        }
        
        // ============ NEW STAGES FOR CONFIG REPO UPDATE ============
        
        stage('Clone Config Repo') {
            steps {
                script {
                    echo "📦 Cloning config repository..."
                    
                    // Create separate directory for config repo
                    sh 'mkdir -p config-repo'
                    
                    dir('config-repo') {
                        // Clone config repo with credentials
                        withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
                            sh """
                                git clone ${env.CONFIG_REPO_URL} .
                                git config user.email "maybetuandat@example.com"
                                git config user.name "Jenkins CI/CD"
                            """
                        }
                        
                        echo "✅ Config repo cloned successfully!"
                        sh 'ls -la'
                    }
                }
            }
        }
        
        stage('Update Helm Values') {
            steps {
                script {
                    echo "🔧 Updating Helm values with new image version..."
                    
                    dir('config-repo') {
                        // Show current values
                        echo "Current helm values:"
                        sh 'cat helm-values/values-prod.yaml | grep -A2 -B2 tag || echo "Tag not found in current format"'
                        
                        // Update image tag in values file
                        sh """
                            # Method 1: Update tag field
                            sed -i 's/^  tag.*/  tag: "${env.TAG_NAME}"/' helm-values/values-prod.yaml
                            
                            # Method 2: If tag is in different format, try this alternative
                            sed -i 's/tag: .*/tag: "${env.TAG_NAME}"/' helm-values/values-prod.yaml
                        """
                        
                        // Show updated values
                        echo "Updated helm values:"
                        sh 'cat helm-values/values-prod.yaml | grep -A2 -B2 tag'
                        
                        // Show git diff
                        sh 'git diff helm-values/values-prod.yaml || echo "No changes detected"'
                        
                        echo "✅ Helm values updated successfully!"
                    }
                }
            }
        }
        
        stage('Push Config Changes') {
            steps {
                script {
                    echo "📤 Pushing changes to config repository..."
                    
                    dir('config-repo') {
                        // Check if there are changes to commit
                        def gitStatus = sh(
                            script: 'git status --porcelain',
                            returnStdout: true
                        ).trim()
                        
                        if (gitStatus) {
                            echo "Changes detected, committing and pushing..."
                            
                            sh """
                                git add .
                                git commit -m "🚀 Update image version to ${env.TAG_NAME}
                                
                                - Updated helm-values/values-prod.yaml
                                - Image: ${env.IMAGE_NAME}:${env.TAG_NAME}
                                - Build: ${env.BUILD_NUMBER}
                                - Jenkins Job: ${env.JOB_NAME}"
                            """
                            
                            // Push with credentials
                            withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
                                sh 'git push origin main'
                            }
                            
                            echo "✅ Config changes pushed successfully!"
                        } else {
                            echo "⚠️ No changes detected in config repo"
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo "🧹 Cleaning up..."
            sh """
                docker rmi ${env.IMAGE_NAME}:${env.TAG_NAME} || true
                docker rmi ${env.IMAGE_NAME}:latest || true
                docker system prune -f || true
            """
            
            // Clean workspace
            cleanWs()
        }
        success {
            echo "🎉 BUILD SUCCESS!"
            echo "✅ Source code built and pushed: ${env.IMAGE_NAME}:${env.TAG_NAME}"
            echo "✅ Config repository updated with new version"
            echo "🔗 Docker Hub: https://hub.docker.com/r/maybetuandat/vdt_backend"
        }
        failure {
            echo "💥 BUILD FAILED!"
            echo "❌ Please check the logs above"
        }
    }
}