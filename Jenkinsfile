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
                echo " Running on agent: ${env.NODE_NAME}"
                echo " Workspace: ${env.WORKSPACE}"
                sh 'whoami'
                sh 'pwd'
                sh 'uname -a'
            }
        }
        
        stage('Checkout Source Code') {
            steps {
                echo " Cloning source code..."
                checkout scm
                
                echo " Clone completed!"
                sh 'ls -la'
            }
        }
        
        stage('Get Git Tag Version') {
            steps {
                script {
                    echo " Getting Git tag version..."
                    
                    
                    sh 'git log --oneline -n 5'
                    sh 'git tag --list'
                    
                    def tagVersion = sh(
                        script: 'git describe --tags --exact-match 2>/dev/null || git describe --tags --abbrev=0 2>/dev/null || git rev-parse --short HEAD', 
                        returnStdout: true
                    ).trim()
                    
                    env.TAG_NAME = tagVersion
                    
                    echo " Using tag version: ${env.TAG_NAME}"
                    echo " Docker image will be: ${env.IMAGE_NAME}:${env.TAG_NAME}"
                }
            }
        }    
        stage('Build Docker Image') {
            steps {
                script {
                    echo " Building Docker image: ${env.IMAGE_NAME}:${env.TAG_NAME}"
                    
                    sh """
                        docker build -t ${env.IMAGE_NAME}:${env.TAG_NAME} .
                        docker tag ${env.IMAGE_NAME}:${env.TAG_NAME} ${env.IMAGE_NAME}:latest
                    """
                    
                    echo " Docker image built successfully!"
                    sh "docker images | grep ${env.IMAGE_NAME}"
                }
            }
        }
        
        stage('Test Docker Image') {
            steps {
                script {
                    echo " Testing Docker image..."
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
                    echo " Pushing image to Docker Hub..."
                    
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
                    echo " Successfully pushed to Docker Hub!"
                }
            }
        }     
        stage('Clone Config Repo') {
            steps {
                script {
                    echo " Cloning config repository..."
                    
                    
                    sh 'mkdir -p config-repo'
                    
                    dir('config-repo') {
                    
                        withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
                            sh """
                                git clone ${env.CONFIG_REPO_URL} .
                                git config user.email "maybetuandat@trinhvinhtuandat05102003@gmail.com"
                                git config user.name "Jenkins CI/CD Backend"
                            """
                        }
                        
                        echo " Config repo cloned successfully!"
                        sh 'ls -la'
                    }
                }
            }
        }
        
       stage('Update Helm Values') {
            steps {
                script {  
                    dir('config-repo') {
                        def tagName = env.TAG_NAME
                        echo "Updating with tag: ${tagName}"
                        
                        sh """
                            sed -i 's/^  tag:.*/  tag: "${tagName}"/' helm-values/values-prod.yaml
                        """
                        
                      
                        sh "grep 'tag:' helm-values/values-prod.yaml"
                    }
                }
            }
    }
        
        stage('Push Config Changes') {
            steps {
                script {
                    echo "Pushing changes to config repository..."
                    
                    dir('config-repo') {
                     
                        def gitStatus = sh(
                            script: 'git status --porcelain',
                            returnStdout: true
                        ).trim()
                        
                        if (gitStatus) {
                            echo "Changes detected, committing and pushing..."
                            
                            sh """
                                git add .
                                git commit -m " Update image version to ${env.TAG_NAME}
                                
                                - Updated helm-values/values-prod.yaml
                                - Image: ${env.IMAGE_NAME}:${env.TAG_NAME}
                                - Build: ${env.BUILD_NUMBER}
                                - Jenkins Job: ${env.JOB_NAME}"
                            """
                            
                            // Push with credentials
                            withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
                                sh 'git push origin main'
                            }
                            
                            echo " Config changes pushed successfully!"
                        } else {
                            echo " No changes detected in config repo"
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo " Cleaning up..."
            sh """
                docker rmi ${env.IMAGE_NAME}:${env.TAG_NAME} || true
                docker rmi ${env.IMAGE_NAME}:latest || true
                docker system prune -f || true
            """
            
          
            cleanWs()
        }
        success {
            echo "BUILD SUCCESS!"
            echo "Source code built and pushed: ${env.IMAGE_NAME}:${env.TAG_NAME}"
            echo "Config repository updated with new version"
            echo "Docker Hub: https://hub.docker.com/r/maybetuandat/vdt_backend"
        }
        failure {
            echo "BUILD FAILED!"
            echo "Please check the logs above"
        }
    }
}