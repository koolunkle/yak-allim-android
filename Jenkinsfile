pipeline {
    agent any

    environment {
        // 안드로이드 SDK 및 JDK 환경변수 설정
        ANDROID_HOME = "${env.ANDROID_HOME ?: 'C:\\Users\\a4336\\AppData\\Local\\Android\\Sdk'}"
        JAVA_HOME = "${env.JAVA_HOME ?: 'C:\\Program Files\\Java\\jdk-17'}"
    }

    stages {
        stage('Checkout') {
            steps {
                // 코드 동기화
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // 자격 증명 주입 및 APK 빌드
                withCredentials([
                    file(credentialsId: 'android-secrets-properties', variable: 'SECRETS_PROPERTIES'),
                    file(credentialsId: 'android-google-services-json', variable: 'GOOGLE_SERVICES_JSON')
                ]) {
                    bat '''
                        copy "%SECRETS_PROPERTIES%" "secrets.properties"
                        copy "%GOOGLE_SERVICES_JSON%" "app\\google-services.json"
                        gradlew.bat clean assembleDebug
                    '''
                }
            }
        }

        stage('Archive') {
            steps {
                // 산출물 아카이빙
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', followSymlinks: false
            }
        }
    }
}