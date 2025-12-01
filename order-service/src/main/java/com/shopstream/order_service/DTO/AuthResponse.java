package com.shopstream.order_service.dto;

public class AuthResponse {
	  private String token;
	  private String username;
	  private Long userid;
	  public Long getUserid() {
		return userid;
	}
	  public void setUserid(Long userid) {
		  this.userid = userid;
	  }
	  public AuthResponse(String token, String username, Long userid) {
		super();
		this.token = token;
		this.username = username;
		this.userid = userid;
	}
	  public AuthResponse() {}
	  public AuthResponse(String token, String username) { this.token = token; this.username = username; }
	  // getters/setters
	  public String getToken() {
		  return token;
	  }
	  public void setToken(String token) {
		  this.token = token;
	  }
	  public String getUsername() {
		  return username;
	  }
	  public void setUsername(String username) {
		  this.username = username;
	  }

}
