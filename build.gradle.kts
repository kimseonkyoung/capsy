// 아래 타입/확장 메서드를 Kotlin DSL에서 쓰기 위해 import
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

// 모든 서브프로젝트(= include한 모듈들)에 공통 적용
subprojects {
    // 각 모듈을 Java 프로젝트로 취급
    // (compileJava, test 등 기본 태스크가 생김)
    apply(plugin = "java")

    // Maven/Gradle 좌표에서 group 역할
    // 나중에 라이브러리 배포할 때 io.capsy:capsy-core:버전 형태가 됨
    group = "io.capsy"

    // 현재 프로젝트 버전
    // SNAPSHOT = 개발 중 버전이라는 의미
    version = "0.1.0-SNAPSHOT"

    // 의존성(라이브러리) 내려받을 저장소
    // 지금은 거의 안 써도, 나중에 Jackson/JUnit/picocli 추가 시 필요
    repositories {
        mavenCentral()
    }

    // Java Toolchain 설정
    // "이 프로젝트는 Java 21 기준으로 빌드한다"를 명시
    // 팀 작업/미래의 나/CI에서 버전 흔들림 방지에 좋음
    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    // 테스트 태스크 공통 설정
    // 나중에 JUnit5 테스트를 바로 붙일 수 있게 기본 준비
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}