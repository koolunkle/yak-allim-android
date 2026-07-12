pipeline {
    agent {
        node {
            label 'built-in'
            // 로컬 프로젝트 작업 공간 지정
            customWorkspace 'C:/Users/a4336/Downloads/yakallim-android'
        }
    }

    environment {
        // 안드로이드 SDK 환경변수 설정
        ANDROID_HOME = 'C:/Users/a4336/AppData/Local/Android/Sdk'
        // Gradle 전용 캐시 및 설정 디렉토리 지정
        GRADLE_USER_HOME = 'C:/Users/a4336/.gradle'
        // JDK 17 지정
        JAVA_HOME = 'C:/Program Files/Java/jdk-17'
        // 실행 경로에 JDK 17 반영
        PATH = "C:/Program Files/Java/jdk-17/bin;${env.PATH}"
    }

    stages {
        stage('Source Sync') {
            steps {
                // 최신 코드 동기화
                bat 'git pull origin main'
            }
        }

        stage('Build APK') {
            steps {
                // 디버그용 APK 빌드
                bat 'gradlew.bat clean assembleDebug'
            }
        }

        stage('Archive APK') {
            steps {
                // 빌드 결과물 아카이빙
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', followSymlinks: false
            }
        }
    }
}