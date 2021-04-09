package pt.unl.fct.di.apdc.firstwebapp.util;

public class LoginData {
	
	public String username;
	public String email;
	public String currentPassword;
	public String password;
	public String passwordConfirmation;
	public String profile;
	public String landline;
	public String phoneNumber;
	public String address;
	public String additionalAddress;
	public String location;
	
	public LoginData() {
		
	}

	public LoginData(String username, String email, String password, String currentPassword, String passwordConfirmation,String profile,String landline,String phoneNumber,String address,String additionalAddress,String location) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.currentPassword = currentPassword;
		this.passwordConfirmation = passwordConfirmation;
		this.profile = profile;
		this.landline = landline;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.additionalAddress = additionalAddress;
		this.location = location;
	}
	
	public boolean validUsername() {
		return username != null && !username.equals("");
	}
	
	//must have at least one numeric character, one lowercase character, one uppercase character and should be between 8 and 20
	public boolean validPassword() {
		boolean isValid = true;
        if (password.length() > 20 || password.length() < 8)
        {
                isValid = false;
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars))
        {
                isValid = false;
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars))
        {
                isValid = false;
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers))
        {
                isValid = false;
        }
        return isValid; 
	}
	
	public boolean validEmail() {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	    return email.matches(regex);
	}
	
    public boolean validConfirmation() {
		return passwordConfirmation.equals(password);
	}
    
    public boolean validProfile() {
		return profile.equals("Público") || profile.equals("Privado") || profile.equals("");
	}
    
    public boolean validLandline() {
    	return landline != null;
	}

    public boolean validPhoneNumber() {
    	return phoneNumber != null;
    }

    public boolean validAdress() {
    	return address != null;
    }
    
    public boolean validAdditionalAdress() {
    	return additionalAddress != null;
    }


    public boolean validLocation() {
    	return location != null;
    }
	
	public boolean validRegistration() {
		return validPassword();           //acabar
	}
	
	
	
}
