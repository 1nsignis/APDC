package pt.unl.fct.di.apdc.firstwebapp.util;

public class UpdateInfo {
	
	public AuthToken token;
	public LoginData data;

	   public UpdateInfo() {
	   }

	   public UpdateInfo(AuthToken token, LoginData data) {
	      this.token = token;
	      this.data = data;
	   }

}
