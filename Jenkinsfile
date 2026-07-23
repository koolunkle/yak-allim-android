pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // 소스 코드 동기화
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // 보안 주입 및 APK 빌드
                withCredentials([
                    file(credentialsId: 'android-secrets-properties', variable: 'SECRETS_PROPERTIES'),
                    file(credentialsId: 'android-google-services-json', variable: 'GOOGLE_SERVICES_JSON')
                ]) {
                    script {
                        if (isUnix()) {
                            // Linux / Docker 환경
                            sh '''
                                rm -f secrets.properties app/google-services.json
                                cp -f "$SECRETS_PROPERTIES" secrets.properties
                                cp -f "$GOOGLE_SERVICES_JSON" app/google-services.json

                                # local.properties 자동 생성
                                if [ ! -f "local.properties" ]; then
                                    SDK_PATH="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
                                    if [ -z "$SDK_PATH" ]; then
                                        if [ -d "/var/jenkins_home/android-sdk" ]; then
                                            SDK_PATH="/var/jenkins_home/android-sdk"
                                        elif [ -d "/opt/android-sdk" ]; then
                                            SDK_PATH="/opt/android-sdk"
                                        elif [ -d "$HOME/Android/Sdk" ]; then
                                            SDK_PATH="$HOME/Android/Sdk"
                                        fi
                                    fi

                                    if [ -n "$SDK_PATH" ]; then
                                        echo "sdk.dir=$SDK_PATH" > local.properties
                                        echo "Generated local.properties with sdk.dir=$SDK_PATH"
                                    else
                                        echo "Warning: ANDROID_HOME environment variable or Android SDK directory not found!"
                                    fi
                                fi

                                # 손상된 build-tools 자동 정리
                                TARGET_SDK="${SDK_PATH:-/var/jenkins_home/android-sdk}"
                                if [ -d "$TARGET_SDK/build-tools" ]; then
                                    for bt in "$TARGET_SDK"/build-tools/*; do
                                        if [ -d "$bt" ] && [ ! -f "$bt/aapt" ] && [ ! -f "$bt/aapt2" ]; then
                                            echo "Removing corrupted build-tools: $bt"
                                            rm -rf "$bt"
                                        fi
                                    done
                                    chmod -R +x "$TARGET_SDK/build-tools" 2>/dev/null || true
                                fi

                                chmod +x gradlew
                                ./gradlew clean assembleDebug
                            '''
                        } else {
                            // Windows 환경
                            withEnv([
                                "ANDROID_HOME=${env.ANDROID_HOME ?: 'C:\\Users\\a4336\\AppData\\Local\\Android\\Sdk'}",
                                "JAVA_HOME=${env.JAVA_HOME ?: 'C:\\Program Files\\Java\\jdk-17'}"
                            ]) {
                                bat '''
                                    if exist secrets.properties del /f /q secrets.properties
                                    if exist app\\google-services.json del /f /q app\\google-services.json
                                    copy /y "%SECRETS_PROPERTIES%" "secrets.properties"
                                    copy /y "%GOOGLE_SERVICES_JSON%" "app\\google-services.json"

                                    if not exist local.properties (
                                        if defined ANDROID_HOME (
                                            echo sdk.dir=%ANDROID_HOME% > local.properties
                                        ) else if defined ANDROID_SDK_ROOT (
                                            echo sdk.dir=%ANDROID_SDK_ROOT% > local.properties
                                        )
                                    )

                                    gradlew.bat clean assembleDebug
                                '''
                            }
                        }
                    }
                }
            }
        }

        stage('Archive') {
            steps {
                // 빌드 산출물(APK) 아카이빙
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', followSymlinks: false
            }
        }
    }
}