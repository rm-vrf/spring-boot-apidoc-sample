package com.mydomain.app.apidoc;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

public class ApiDocSampleApplicationTests {

	@Test
	public void testDefaultSettings() {
		Assert.assertTrue(SpringApplication.exit(SpringApplication.run(Main.class)) == 0);
	}

}