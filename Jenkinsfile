pipeline {
    agent any

    parameters {
        booleanParam(
            name: 'CLEAN_BUILD',
            defaultValue: false,
            description: '선택 시 기존 빌드 캐시를 삭제하고 클린 빌드를 수행합니다.'
        )
    }

    stages {
        stage('Build') {
            steps {
                // 보안 자격 증명 주입 및 빌드
                withCredentials([
                    file(credentialsId: 'android-secrets-properties', variable: 'SECRETS_PROPERTIES'),
                    file(credentialsId: 'android-google-services-json', variable: 'GOOGLE_SERVICES_JSON')
                ]) {
                    script {
                        def cleanOption = params.CLEAN_BUILD ? 'clean' : ''

                        if (isUnix()) {
                            // Unix/Linux 환경
                            sh """
                                rm -f secrets.properties app/google-services.json
                                cp -f "\$SECRETS_PROPERTIES" secrets.properties
                                cp -f "\$GOOGLE_SERVICES_JSON" app/google-services.json

                                # local.properties 설정
                                SDK_PATH="\${ANDROID_HOME:-/var/jenkins_home/android-sdk}"
                                echo "sdk.dir=\$SDK_PATH" > local.properties

                                # 불완전한 build-tools 정리
                                if [ -d "\$SDK_PATH/build-tools" ]; then
                                    for bt in "\$SDK_PATH"/build-tools/*; do
                                        if [ -d "\$bt" ] && [ ! -f "\$bt/aapt" ] && [ ! -f "\$bt/aapt2" ]; then
                                            echo "Removing incomplete build-tools: \$bt"
                                            rm -rf "\$bt"
                                        fi
                                    done
                                    chmod -R +x "\$SDK_PATH/build-tools" 2>/dev/null || true
                                fi

                                chmod +x gradlew
                                ./gradlew ${cleanOption} assembleDebug
                            """
                        } else {
                            // Windows 환경
                            withEnv([
                                "ANDROID_HOME=${env.ANDROID_HOME ?: 'C:\\Users\\a4336\\AppData\\Local\\Android\\Sdk'}",
                                "JAVA_HOME=${env.JAVA_HOME ?: 'C:\\Program Files\\Java\\jdk-17'}"
                            ]) {
                                bat """
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

                                    gradlew.bat ${cleanOption} assembleDebug
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Archive') {
            steps {
                // APK 아카이빙
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', followSymlinks: false
            }
        }
    }
}