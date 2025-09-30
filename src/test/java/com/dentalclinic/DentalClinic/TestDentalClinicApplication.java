package com.dentalclinic.DentalClinic;

import org.springframework.boot.SpringApplication;

public class TestDentalClinicApplication {

	public static void main(String[] args) {
		SpringApplication.from(DentalClinicApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
