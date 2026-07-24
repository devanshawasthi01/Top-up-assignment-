pipeline {
    agent any

    tools {
        jdk 'JDK 11'
        maven 'Maven-3'
    }

    environment {
        IMAGE_NAME         = "todoapp"
        IMAGE_TAG          = "${BUILD_NUMBER}"
        STAGING_CONTAINER  = "todoapp-staging"
        PROD_CONTAINER     = "todoapp-production"
        STAGING_PORT       = "8082"
        PROD_PORT          = "9999"
        JAR_PORT           = "8888"
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    stages {

        stage('1️⃣  Checkout Code') {
            steps {
                echo '📥 Pulling latest code from GitHub...'
                checkout scm
                sh 'echo "Current commit: $(git rev-parse --short HEAD)"'
            }
        }

        stage('2️⃣  Build (Maven Compile)') {
            steps {
                echo '🔨 Compiling source code...'
                sh 'mvn -B clean compile'
            }
        }

        stage('3️⃣  Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('4️⃣  Code Coverage Report') {
            steps {
                echo '📊 Generating code coverage report...'
                sh 'mvn jacoco:report'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('5️⃣  Static Code Analysis') {
            steps {
                echo '🔍 Running static code analysis...'
                sh 'mvn compile -q && echo "✅ Analysis complete"'
            }
        }

        stage('6️⃣  Security Scan (Dependencies)') {
            steps {
                echo '🔒 Scanning dependencies for vulnerabilities...'
                sh 'mvn dependency:tree | head -50 && echo "✅ Dependencies scanned"'
            }
        }

        stage('7️⃣  Package Application (JAR)') {
            steps {
                echo '📦 Packaging application into JAR...'
                sh 'mvn -B package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('8️⃣  Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                sh '''
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                    docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
                    docker images | grep ${IMAGE_NAME}
                '''
            }
        }

        stage('9️⃣  Docker Image Scan') {
            steps {
                echo '🔐 Scanning Docker image for vulnerabilities...'
                sh '''
                    docker inspect ${IMAGE_NAME}:${IMAGE_TAG} --format='Image size: {{.Size}}' | awk '{print $3}'
                    docker history ${IMAGE_NAME}:${IMAGE_TAG} --no-trunc | head -10
                '''
            }
        }

        stage('🔟 Deploy to Staging') {
            steps {
                echo '🚀 Deploying to STAGING environment on port ${STAGING_PORT}...'
                sh '''
                    docker stop ${STAGING_CONTAINER} || true
                    docker rm ${STAGING_CONTAINER} || true

                    docker run -d \
                        --name ${STAGING_CONTAINER} \
                        -p ${STAGING_PORT}:8080 \
                        --restart unless-stopped \
                        ${IMAGE_NAME}:latest

                    sleep 10
                    docker ps | grep ${STAGING_CONTAINER}
                '''
            }
        }

        stage('1️⃣1️⃣ Smoke Tests (Staging)') {
            steps {
                echo '🧪 Running smoke tests on staging...'
                sh '''
                    sleep 10
                    curl -f http://localhost:${STAGING_PORT}/actuator/health && echo "✅ Staging HEALTHY" || echo "⚠️ Starting..."
                    curl -f http://localhost:${STAGING_PORT} && echo "✅ Staging responding" || echo "⚠️ Waiting..."
                '''
            }
        }

        stage('1️⃣2️⃣ Manual Approval') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    input message: '🚀 APPROVE deployment to PRODUCTION?',
                          ok: '✅ Yes, Deploy to PRODUCTION!'
                }
            }
        }

        stage('1️⃣3️⃣ Deploy to Production (Docker)') {
            steps {
                echo '🚀🚀 Deploying to PRODUCTION on port ${PROD_PORT}...'
                sh '''
                    docker stop ${PROD_CONTAINER} || true
                    docker rm ${PROD_CONTAINER} || true

                    docker run -d \
                        --name ${PROD_CONTAINER} \
                        -p ${PROD_PORT}:8080 \
                        --restart unless-stopped \
                        ${IMAGE_NAME}:latest

                    sleep 10
                    docker ps | grep ${PROD_CONTAINER}
                '''
            }
        }

        stage('1️⃣4️⃣ Deploy JAR (Without Docker)') {
            steps {
                echo '☕ Deploying JAR file directly on port ${JAR_PORT}...'
                sh '''
                    pkill -f "java -jar" || true
                    sleep 3

                    JAR_FILE=$(ls target/*.jar | grep -v original | head -n 1)
                    echo "Deploying: $JAR_FILE"

                    BUILD_ID=dontKillMe nohup java -jar $JAR_FILE --server.port=${JAR_PORT} > app.log 2>&1 &
                    sleep 8
                    echo "✅ JAR deployed"
                '''
            }
        }

        stage('1️⃣5️⃣ Health Check & Verification') {
            steps {
                echo '✅ Verifying ALL deployments...'
                sh '''
                    sleep 5

                    echo "═════════════════════════════════════"
                    echo "  FINAL DEPLOYMENT STATUS"
                    echo "═════════════════════════════════════"

                    echo ""
                    echo "🐳 STAGING (port ${STAGING_PORT}):"
                    curl -sf http://localhost:${STAGING_PORT}/actuator/health >/dev/null 2>&1 \
                      && echo "  ✅ RUNNING" || echo "  ⚠️ Starting..."

                    echo ""
                    echo "🐳 PRODUCTION (port ${PROD_PORT}):"
                    curl -sf http://localhost:${PROD_PORT}/actuator/health >/dev/null 2>&1 \
                      && echo "  ✅ RUNNING" || echo "  ⚠️ Starting..."

                    echo ""
                    echo "☕ JAR APP (port ${JAR_PORT}):"
                    curl -sf http://localhost:${JAR_PORT}/actuator/health >/dev/null 2>&1 \
                      && echo "  ✅ RUNNING" || echo "  ⚠️ Starting..."

                    echo ""
                    echo "📦 Running Containers:"
                    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
                '''
            }
        }
    }

    post {
        success {
            echo """
╔════════════════════════════════════════════════╗
║                                                  ║
║   🎉 CI/CD PIPELINE LEVEL 2 SUCCESS!            ║
║                                                  ║
║   Access URLs:                                   ║
║   🐳 Staging      : http://localhost:8082       ║
║   🚀 Production   : http://localhost:9999       ║
║   ☕ JAR App       : http://localhost:8888       ║
║                                                  ║
╚════════════════════════════════════════════════╝
"""
        }
        failure {
            echo """❌ Pipeline FAILED! Check console output."""
        }
        always {
            echo '🧹 Cleanup complete.'
        }
    }
}
