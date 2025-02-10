package com.example.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Customer;
import com.example.repo.CustomerRepository;
import com.example.service.JwtService;

@RestController
public class CustomerRestController {

	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private PasswordEncoder pwdEncoder;
	@Autowired
	private CustomerRepository cRepo;
	@Autowired
	private JwtService jwt;
	
	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome...";
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Customer c){
		
		UsernamePasswordAuthenticationToken token = 
				new UsernamePasswordAuthenticationToken(c.getEmail(),c.getPwd());
		
	
		try {
			Authentication authenticate =authManager.authenticate(token);
			
			if(authenticate.isAuthenticated()) {
				String jwtToken = jwt.generateToken(c.getName());
				return new ResponseEntity<>(jwtToken,HttpStatus.OK);
			}
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>("Invalide Credentials", HttpStatus.BAD_REQUEST);
		
		/**boolean status = authenticate.isAuthenticated();
		if(status) {
			return new ResponseEntity<String>("Welcome",HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Failed",HttpStatus.BAD_REQUEST);
		}**/
	}
	
	@PostMapping("/register")
	public String registerCustomer(@RequestBody Customer c){
		
		String encodePwd = pwdEncoder.encode(c.getPwd());
		c.setPwd(encodePwd);
		cRepo.save(c);
		return "User Registered";
		
		/**boolean status = customerService.saveCustomer(c);
		if(status) {
			return new ResponseEntity<>("Success", HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}**/	
	}
}