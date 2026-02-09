package pl.tul.discountmanagement.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static pl.tul.discountmanagement.shared.infrastructure.constant.ApplicationProfiles.INTEGRATION_TEST_PROFILE;

/**
 * Configuration class for containerized instance of PostgreSQL database.
 * Meant to be used in integration test scenarios.
 */
@TestConfiguration(proxyBeanMethods = false)
@Profile(INTEGRATION_TEST_PROFILE)
class TestcontainersConfiguration {

	private static final String DATABASE_IMAGE_NAME = "postgres:16.4";
	private static final String DATABASE_NAME = "integration-test-db";
	private static final String DATABASE_USER = "postgres";
	private static final String DATABASE_PASSWORD = "postgres";

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse(DATABASE_IMAGE_NAME))
				.withDatabaseName(DATABASE_NAME)
				.withUsername(DATABASE_USER)
				.withPassword(DATABASE_PASSWORD);
	}

}
