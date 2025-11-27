// 순수 자바 - 외부 의존성 없음
dependencies {
    // 테스트 의존성만
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
