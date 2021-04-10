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

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {
	
	private static final Logger LOG = Logger.getLogger(LogoutResource.class.getName());
	
	private final Datastore datastore = (Datastore)DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

    public LogoutResource() { } //empty constructor
    
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doLogout(AuthToken token) {
    	Transaction txn = datastore.newTransaction();

        try {
        	
           Key tokenKey = datastore.newKeyFactory()
          		 .addAncestor(PathElement.of("User", token.user))
          		 .setKind("Token")
          		 .newKey("token");
           
           Entity tokenEnt = this.datastore.get(tokenKey);
           
         
           
           if (!this.validToken(token, tokenEnt)) {
              return Response.status(Status.FORBIDDEN).entity("Invalid Token").build();
           }

           txn.delete(tokenKey);
           txn.commit();
           return Response.ok().entity("Successfully logged out").build();
           
        } finally {
           if (txn.isActive()) {
              txn.rollback();
           }
        }
    }
    
    
    private boolean validToken(AuthToken token, Entity tokenEnt) {
        return tokenEnt != null && token != null 
      		  && token.verifier.equals(tokenEnt.getString("id")) 
      		  && tokenEnt.getLong("expirationData") > System.currentTimeMillis();
     }
    
    
}