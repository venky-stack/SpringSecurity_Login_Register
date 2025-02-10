package com.example.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.example.service.CustomerService;
import com.example.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AppFilter {
	
	@Autowired
	private JwtService jwtService;
	@Autowired
	CustomerService userDetailsService;
	
	 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

	        String authHeader = request.getHeader("Authorization");
	        String token = null;
	        String username = null;
	        if(authHeader != null && authHeader.startsWith("Bearer ")){
	            token = authHeader.substring(7);
	            username = jwtService.extractUsername(token);
	        }

	        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
	            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	            if(jwtService.validateToken(token, userDetails)){
	                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	            }
	        }
	        filterChain.doFilter(request, response);
	    }
}