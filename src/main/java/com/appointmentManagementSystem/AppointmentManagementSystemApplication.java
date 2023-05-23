package com.appointmentManagementSystem;


import com.appointmentManagementSystem.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class AppointmentManagementSystemApplication implements CommandLineRunner {
	@Resource
	RoleService roleService;
	public static void main(String[] args) {SpringApplication.run(AppointmentManagementSystemApplication.class, args);}

	@Override
	public void run(String... args) throws Exception
	{
		roleService.init();
	}

}
