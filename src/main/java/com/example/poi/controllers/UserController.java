package com.example.poi.controllers;


import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	
		static final String DOWNLOAD = "attachment; filename=data.xlsx";
	
		@GetMapping(path="/writeFile")
		public void writeData()
		{
			userService.writeData();  
	    } 
		
		@GetMapping(path="/download")
		public ResponseEntity<ByteArrayResource> downloadTemplate() {
	            ByteArrayOutputStream stream = userService.download();
	            HttpHeaders header = new HttpHeaders();
	            header.setContentType(new MediaType("application", "force-download"));
	            header.set(HttpHeaders.CONTENT_DISPOSITION, DOWNLOAD);
	            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),
	                    header, HttpStatus.CREATED);
	    }

		@PostMapping(path="/createTable")
		public void readData()
		{
			userService.readData();
		}
}
