package com.example.poi.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poi.services.UserService;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
		@GetMapping(path="/writeFile")
		public void writeData() throws DocumentException
		{
			userService.writeData();
		}
		@PostMapping(path="/createTable")
		public void readData()
		{
			userService.readData();
		}
}
