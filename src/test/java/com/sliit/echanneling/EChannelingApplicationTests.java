package com.sliit.echanneling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class EChannelingApplicationTests {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void testPasswordMatch() {
		String rawPassword = "password";
		String seedHash = "$2a$10$6UFMj5Nxe/S6.W6kWeMo3eBy3YON6GI3WDyvCIq5VpETMvg4r4Ixy";
		assertTrue(passwordEncoder.matches(rawPassword, seedHash), "Seed hash must match 'password'");
	}

}
