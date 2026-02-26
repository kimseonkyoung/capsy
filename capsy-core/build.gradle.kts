plugins {
    // core는 "실행 앱"이 아니라 "로직 모듈"이므로 java만 있으면 충분
    java
}

dependencies {
    // JUnit 5: 테스트 실행 엔진 + API
    // testImplementation = 테스트 컴파일/실행에만 포함, 배포 jar에는 안 들어감
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    // Gradle 9 + JUnit 5 조합에서 런타임 런처가 명시적으로 필요
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}