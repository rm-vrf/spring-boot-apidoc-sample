package com.mydomain.app.apidoc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("测试接口")
@RestController
public class DemoController {

	@ApiOperation("获取消息")
	@GetMapping(value = "/message/{id}")
	public ResponseEntity<Message> getMessage(@ApiParam("编号") @PathVariable("id") String id) {
		
		Message body = new Message();
		body.setId(id);
		body.setBody("床前明月光");
		body.setReceiver("dupu");
		body.setSender("libai");
		body.setTime(new Date());
		body.setTitle("静夜思");
		
		return new ResponseEntity<Message>(body, HttpStatus.OK);
	}

	@ApiOperation("查询消息")
	@PostMapping(value = "/message/_search")
	public ResponseEntity<List<Message>> searchMessage(
			@ApiParam("查询条件") @RequestParam(value = "query", required = false) String query) {
		
		List<Message> body = new ArrayList<>();
		
		return new ResponseEntity<List<Message>>(body, HttpStatus.OK);
	}
	
	@ApiOperation("创建消息")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Created")
	})
	@PostMapping(value="/message")
	public ResponseEntity<String> postMessage(@ApiParam("消息") @RequestBody Message message) {
		
		String id = UUID.randomUUID().toString();
		return new ResponseEntity<String>(id, HttpStatus.OK);
	}

}
