package com.example.blog;

import com.example.PostgresContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BlogApplicationTests extends PostgresContainerTest {

	@Test
	void contextLoads() {
	}

}
