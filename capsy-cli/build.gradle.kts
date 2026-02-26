import org.gradle.api.tasks.JavaExec // run 태스크 타입(JavaExec) 참조용 import

plugins {
    java
    application
}

dependencies {
    implementation(project(":capsy-core"))
}

application {
    mainClass.set("io.capsy.cli.CapsyApp")
    // installDist 결과물의 실행 파일 이름을 capsy-cli 대신 capsy 로 고정
    // build/install/capsy/bin/capsy 로 생성됨
    applicationName = "capsy"
}

// :capsy-cli:run 실행 시 작업 디렉토리를 레포 루트(capsy)로 고정
// 그래서 Path.of("") 기준으로 .capsy가 capsy/.capsy 에 생성됨
tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}