package com.mydomain.app.apidoc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
public class DemoController {

	@GetMapping(value="/test")
	public ResponseEntity<List<String>> getList(WebRequest request, 
			@RequestParam(value = "query", required = false) String query) {
		
		List<String> body = new ArrayList<>();
		body.add("hello world");
		return new ResponseEntity<List<String>>(body, HttpStatus.OK);
	}
}
