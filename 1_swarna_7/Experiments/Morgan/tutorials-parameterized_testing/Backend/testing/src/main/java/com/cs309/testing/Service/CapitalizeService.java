package com.cs309.testing.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs309.testing.Model.StringEntity;
import com.cs309.testing.Model.StringRepo;

@Service
public class CapitalizeService {
	
	@Autowired
	StringRepo sRepo;
	
	/**
	 * Uses StringBuilder to reverse the given string
	 * @param str	Given string
	 * @return	Reversed string
	 */
	public List<StringEntity> capitalize(String str) {
		StringEntity s = new StringEntity();
		String c = str.toUpperCase();
		s.setData(c);
		sRepo.save(s);
		return sRepo.findAll();
	}
}
