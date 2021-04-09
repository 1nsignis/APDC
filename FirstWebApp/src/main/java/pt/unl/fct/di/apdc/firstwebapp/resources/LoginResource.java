package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Datastore datastore = (Datastore)DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

    public LoginResource() { } //empty constructor
    
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doLogin(LoginData data) {
    	/*
    	LOG.fine("Login attempt by user: " + data.username);
    	
    	if(data.username.equals("diogo") && data.password.equals("1234")) {
    	    AuthToken at = new AuthToken(data.username, "USER");
    	    return Response.ok(g.toJson(at)).build();
    	}
    	
    	return Response.status(Status.FORBIDDEN).entity("Incorrect username or password.").build();
    	*/
    	
    	
    	Transaction txn = datastore.newTransaction();
        
        try {
      	 Key userKey = datastore.newKeyFactory()
      			 .setKind("User")
      			 .newKey(data.username);
      	 
      	Key tokenKey = datastore.newKeyFactory()
           		.addAncestor(PathElement.of("User", data.username))
           		.setKind("Token")
           		.newKey("token");
         
           Entity user = this.datastore.get(userKey);
           
           if (user != null) {
              String hashedPWD = user.getString("password");
              if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
                 
              	AuthToken token = new AuthToken(data.username, user.getString("role"));
              	
              	Entity tokenEnt = Entity.newBuilder(tokenKey)
                		   .set("id", token.verifier)
                		   .set("cretionData", token.validFrom)
                		   .set("expirationData", token.validTo)
                		   .set("role", user.getString("role"))
                		   .build();
              	
              	txn.put(user, tokenEnt);
                txn.commit();
              
              	return Response.ok(this.g.toJson(token)).build();
              }
              return Response.status(Status.FORBIDDEN).entity("Incorrect password.").build();
           }
           return Response.status(Status.FORBIDDEN).entity("Incorrect username.").build();
        } finally {
           if (txn.isActive()) {
              txn.rollback();
           }
        }
        
    }
    
    @GET
    @Path("/{username}")
    public Response checkUsernameAvailable(@PathParam("username") String username) {
    	Key userKey = datastore.newKeyFactory()
     			 .setKind("User")
     			 .newKey(username);
    	
    	Entity user = this.datastore.get(userKey);
    	
        if(user == null) {
            return Response.ok().entity("Username available").build();
        } else {
            return Response.ok().entity("Username not available").build();
        }
    }
    
}
