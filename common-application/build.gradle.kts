dependencies {
    // common-domain 의존
    api(project(":common-domain"))
    
    // 테스트 의존성
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
