// pipeline {
//     agent any
//     environment {
        
//         IMAGE_NAME = 'maybetuandat/vdt_backend'
        
//         DATABASE_NAME = 'student_management'
//         DATABASE_USER = 'postgres'
//         DATABASE_PASSWORD = '123456'
//         DATABASE_HOST = '192.168.122.93' 
//         DATABASE_PORT = '5432'
        
        
//         DOCKER_HUB_CREDENTIALS = 'dockerhub_credential' 
//         GITHUB_CREDENTIALS = 'github-pat' 
        
        
//         CONFIG_REPO_URL = 'https://github.com/Maybetuandat/vdt_2025_backend_config' 
//     }
    
//     stages {
//         stage('Checkout Backend Code') {
//             steps {
//                 script {
//                     echo "Clone backend code from branch ${env.BRANCH_NAME}"
//                     checkout scm
//                 }
//                 script {
//                     // Lấy tag version từ Git
//                     def tagVersion = sh(script: 'git describe --tags --abbrev=0 2>/dev/null || echo "1.0"', returnStdout: true).trim()
//                     env.TAG_NAME = tagVersion
//                     echo "Backend tag version: ${env.TAG_NAME}"
//                 }
//             }
//         }
        
//         stage('Build Backend Image') {
//             steps {
//                 script {
//                     echo "Building Backend Image: ${env.IMAGE_NAME}:${env.TAG_NAME}"
//                     // Build Spring Boot application với Dockerfile
//                     sh "docker build -t ${env.IMAGE_NAME}:${env.TAG_NAME} ."
//                 }
//             }
//         }
        
//         stage('Run Backend Tests') {
//             steps {
//                 script {
//                     echo "Running Spring Boot tests..."
//                     // Chạy Maven test trong Docker container
//                     sh """
//                         docker run --rm \
//                         -v \$(pwd):/workspace \
//                         -w /workspace \
//                         maven:3.9.6-amazoncorretto-21 \
//                         mvn clean test -DskipTests=false
//                     """
//                 }
//             }
//         }
        
//         stage('Push Backend Image to Docker Hub') {
//             steps {
//                 script {
//                     // Login to Docker Hub
//                     withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
//                         sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
//                     }
                    
//                     // Push Docker image
//                     sh "docker push ${env.IMAGE_NAME}:${env.TAG_NAME}"
//                     echo "Successfully pushed ${env.IMAGE_NAME}:${env.TAG_NAME} to Docker Hub"
//                 }
//             }
//         }
        
//         stage('Clone Config Repo') {
//             when {
//                 expression { env.CONFIG_REPO_URL != '' }
//             }
//             steps {
//                 script {
//                     echo "Cloning config repository..."
//                     // Clone config repo trong thư mục riêng
//                     sh 'rm -rf config-repo'
//                     withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
//                         sh "git clone ${env.CONFIG_REPO_URL} config-repo"
//                     }
//                 }
//             }
//         }
        
//         stage('Update Backend Helm Values') {
//             when {
//                 expression { env.CONFIG_REPO_URL != '' }
//             }
//             steps {
//                 script {
//                     echo "Updating backend helm values with new image tag: ${env.TAG_NAME}"
//                     dir('config-repo') {
//                         // Update backend image tag trong values file
//                         sh """
//                             sed -i 's/^  tag:.*/  tag: "${env.TAG_NAME}"/' helm-values/values-prod.yaml
//                         """
                        
//                         // Verify thay đổi
//                         sh 'cat helm-values/values-prod.yaml | grep -A 5 "image:"'
//                     }
//                 }
//             }
//         }
        
//         stage('Push Config Changes') {
//             when {
//                 expression { env.CONFIG_REPO_URL != '' }
//             }
//             steps {
//                 script {
//                     dir('config-repo') {
//                         // Configure git user
//                         sh '''
//                             git config user.email "jenkins@vdt.com"
//                             git config user.name "Jenkins CI"
//                         '''
                        
//                         // Add và commit changes
//                         sh 'git add helm-values/values-prod.yaml'
//                         sh "git commit -m 'Update backend image tag to ${env.TAG_NAME}' || echo 'No changes to commit'"
                        
//                         // Push changes
//                         withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
//                             sh 'git push origin main || git push origin master'
//                         }
                        
//                         echo "Successfully updated config repo with backend image tag: ${env.TAG_NAME}"
//                     }
//                 }
//             }
//         }
//     }
    
//     post {
//         always {
//             script {
//                 // Clean up
//                 sh "docker rmi ${env.IMAGE_NAME}:${env.TAG_NAME} || true"
//                 sh 'rm -rf config-repo'
//                 cleanWs()
//             }
//         }
//         success {
//             echo 'Backend pipeline completed successfully!'
//             echo "Backend image ${env.IMAGE_NAME}:${env.TAG_NAME} has been built and pushed"
//         }
//         failure {
//             echo 'Backend pipeline failed. Please check the logs above.'
//         }
//     }
// }
pipeline {
    agent any
    
    stages {
        stage('Checkout Source Code') {
            steps {
                echo "Cloning source code..."
                checkout scm
                
                echo "Clone completed!"
                sh 'ls -la'
            }
        }
    }
}
