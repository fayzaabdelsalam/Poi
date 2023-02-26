package com.example.poi.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="user")
public class UserEntity  {
	
		@Id
		private Integer id;
		@Column(name="email")
		private String email;
		@Column(name="password")
		private String password;
		
}
