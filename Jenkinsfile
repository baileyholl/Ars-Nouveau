#!/usr/bin/env groovy

pipeline {
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '10'))
    }
    agent any
    tools {
        jdk "jdk-21"
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                echo 'Building'
                sh './gradlew build'
            }
        }
        stage('Publish') {
            steps {
                echo 'Deploying to Maven'
                sh './gradlew publish'
            }
        }
    }
}