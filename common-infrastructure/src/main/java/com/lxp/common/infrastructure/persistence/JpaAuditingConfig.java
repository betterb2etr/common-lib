package com.lxp.common.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정
 * 이 설정을 사용하는 프로젝트에서 @Import(JpaAuditingConfig.class) 또는
 * ComponentScan으로 포함시켜야 함
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
