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

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveResource {
	
	private static final Logger LOG = Logger.getLogger(RemoveResource.class.getName());
	
	private final Datastore datastore = (Datastore)DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

    public RemoveResource() { } //empty constructor
    
    @POST
    @Path("/{user}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doRemove(@PathParam("user") String user, AuthToken token) {
    	Transaction txn = datastore.newTransaction();

        try {
           Key tokenKey = datastore.newKeyFactory()
          		 .addAncestor(PathElement.of("User", token.user))
          		 .setKind("Token")
          		 .newKey("token");
           Entity userRemovingToken = this.datastore.get(tokenKey);
           Key userKey = datastore.newKeyFactory()
          		 .setKind("User")
          		 .newKey(user);
           Entity userToBeRemoved = this.datastore.get(userKey);
           
           if (!this.validUser(userToBeRemoved) || !this.validToken(token, userRemovingToken)) {
             return Response.status(Status.FORBIDDEN).entity("erro").build();
           }
           
           if (this.validRole("USER", userRemovingToken) && token.user.equals(user)) {
        	   txn.delete(userKey, tokenKey);
               txn.commit();
               return Response.ok().entity("User Deleted: " + user).build();
           }
           
           if ((this.validRole("GBO", userRemovingToken) || this.validRole("GA", userRemovingToken)) && userToBeRemoved.getString("role").equals("USER")) {
        	   txn.delete(userKey, tokenKey);
               txn.commit();
               return Response.ok().entity("User Deleted: " + user).build();
           }
           
           
           
          
           return Response.status(Status.FORBIDDEN).entity("erro").build();
           
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
    
    private boolean validRole(String role, Entity tokenEnt) {
    	return tokenEnt.getString("role").equals(role);
    }
    
    private boolean validUser(Entity user) {
    	return user != null;
    }
    
    
}
