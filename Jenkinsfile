pipeline {
agent any

tools {
    jdk 'JDK 21'
    maven 'MAVEN3'
}

environment {
    IMAGE_NAME = "todoapp"
    CONTAINER_NAME = "todoapp-container"
    PORT = "9999"
    AWS_REGION = "eu-north-1"
    ECR_REPO = "876724398547.dkr.ecr.eu-north-1.amazonaws.com/dev/dev"
}

stages {

    stage('Checkout') {
        steps {
            checkout scm
        }
    }

    stage('Build (Maven)') {
        steps {
            sh 'mvn -B clean package -DskipTests'
        }
    }

    stage('Docker Build') {
        steps {
            sh 'docker build -t $IMAGE_NAME .'
        }
    }

    stage('ECR Login') {
        steps {
            sh '''
            aws ecr get-login-password --region $AWS_REGION | \
            docker login --username AWS --password-stdin $ECR_REPO
            '''
        }
    }

    stage('Tag Image') {
        steps {
            sh 'docker tag $IMAGE_NAME:latest $ECR_REPO:latest'
        }
    }

    stage('Push to ECR') {
        steps {
            sh 'docker push $ECR_REPO:latest'
        }
    }

    stage('Deploy from ECR') {
        steps {
            sh '''
            echo "Stopping old container..."
            docker stop $CONTAINER_NAME || true
            docker rm $CONTAINER_NAME || true

            echo "Pulling latest image from ECR..."
            docker pull $ECR_REPO:latest

            echo "Running container on port 9999..."
            docker run -d -p $PORT:9999 --name $CONTAINER_NAME $ECR_REPO:latest
            '''
        }
    }

    stage('Deploy WITHOUT Docker') {
        steps {
            sh '''
            echo "Stopping old JAR app (if running)..."
            pkill -f "java -jar" || true

            echo "Finding JAR file..."
            JAR_FILE=$(ls target/*.jar | head -n 1)

            echo "Running JAR on port 8888..."
            nohup java -jar $JAR_FILE --server.port=8888 > app.log 2>&1 &

            echo "JAR deployed successfully on port 8888!"
            '''
        }
    }
}

post {
    success {
        echo "Deployment successful!"
        echo "Docker App: http://13.60.34.63:9999"
        echo "JAR App: http://13.60.34.63:8888"
    }
    failure {
        echo "Pipeline FAILED"
    }
}

}
