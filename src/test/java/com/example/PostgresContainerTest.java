package com.example;

//@Testcontainers
//@ExtendWith(SpringExtension.class)
//public class PostgresContainerTest {
//
//  // 테스트 전반에서 단 한 번만 재사용되는 컨테이너
//  @Container
//  protected static final PostgreSQLContainer<?> POSTGRES =
//      new PostgreSQLContainer<>("postgres:15-alpine")
//          .withDatabaseName("testdb")
//          .withUsername("test")
//          .withPassword("test");
//  static {
//  }
//  @DynamicPropertySource
//  static void registerProps(DynamicPropertyRegistry r) {
//    r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
//    r.add("spring.datasource.username", POSTGRES::getUsername);
//    r.add("spring.datasource.password", POSTGRES::getPassword);
//    r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
//    r.add("spring.jpa.hibernate.ddl-auto", () -> "create");
//  }
//}

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
public abstract class PostgresContainerTest {

  // static으로 선언하고 수동으로 시작
  protected static final PostgreSQLContainer<?> POSTGRES;

  static {
    POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true);  // 컨테이너 재사용 활성화

    POSTGRES.start();  // 명시적으로 시작
  }

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    r.add("spring.datasource.username", POSTGRES::getUsername);
    r.add("spring.datasource.password", POSTGRES::getPassword);
    r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    r.add("spring.sql.init.mode", () -> "always");
    r.add("spring.jpa.hibernate.ddl-auto", () -> "none");

    //    r.add("spring.jpa.hibernate.ddl-auto", () -> "create");
  }
}
