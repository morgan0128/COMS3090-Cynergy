package com.cs309.testing.Model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name="reverse")
@Data
public class StringEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@NotNull
	@Column
	private String data;
	
	@NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "posted_at")
    private Date postedAt = new Date();

 
	public void setData(String s) {
		data = s;
	}
	public String getData() {
		return data;
	}
	
	/* No-args constructor, setters, and getters are automatically generated thanks to @Data */
}
