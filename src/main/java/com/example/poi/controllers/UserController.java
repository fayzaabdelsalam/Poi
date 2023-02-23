package com.example.poi.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poi.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
		@GetMapping
		public String writeData() throws Exception
		{
			return userService.writeData();
		}
		
		@PostMapping
		public void readData() throws IOException
		{
			userService.readData();
		}
}
