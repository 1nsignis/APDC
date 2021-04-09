package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.UUID;

public class AuthToken {
	
	public static final long EXPIRATION_TIME = 1000*60*60*2; //2h
    	
	public String user;
	public String role;
	public long validFrom;
	public long validTo;
	public String verifier;
	
	public AuthToken() {
		
	}
	
	public AuthToken(String username, String role) {
	    this.user = username;
	    this.role = role;
	    this.validFrom = System.currentTimeMillis();
	    this.validTo = this.validFrom + AuthToken.EXPIRATION_TIME;
	    this.verifier = UUID.randomUUID().toString();
	}


}
