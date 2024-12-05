package pl.tul.discountmanagement;

import org.springframework.boot.SpringApplication;

public class TestDiscountManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(DiscountManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
