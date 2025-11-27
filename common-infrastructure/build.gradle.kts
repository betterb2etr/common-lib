dependencies {
    // common-application 의존 (transitively common-domain도 포함)
    api(project(":common-application"))
    
    // Spring 의존성
    implementation("org.springframework:spring-context:6.1.0")
    implementation("org.springframework:spring-web:6.1.0")
    
    // JPA 의존성
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.0")
    
    // 테스트 의존성
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.springframework:spring-test:6.1.0")
}

tasks.test {
    useJUnitPlatform()
}
