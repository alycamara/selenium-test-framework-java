pipeline {
    agent any

    tools {
        maven 'maven-3.9.9'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Clonage du projet depuis GitHub...'
                git branch: 'main',
                    url: 'https://github.com/alycamara/selenium-test-framework-java.git'
            }
        }

        stage('Clean') {
            steps {
                echo 'Nettoyage du workspace Maven...'
                sh 'mvn clean'
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation et installation du projet...'
                sh 'mvn install -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Exécution des tests Selenium sur Grid...'
                sh 'mvn test'
            }
        }

        stage('Report') {
            steps {
                echo 'Publication du rapport Extent...'
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/ExtentReport',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'Extent Report'
                ])
            }
        }
    }

    post {
        always {
            echo 'Publication des résultats TestNG...'
            junit 'target/surefire-reports/*.xml'
        }

        success {
            echo 'BUILD SUCCESS'
        }

        failure {
            echo 'BUILD FAILED'
        }
    }
}