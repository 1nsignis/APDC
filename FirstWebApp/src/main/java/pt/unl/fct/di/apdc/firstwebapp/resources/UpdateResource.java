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
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.UpdateInfo;

@Path("/update")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateResource {
	
	private static final Logger LOG = Logger.getLogger(UpdateResource.class.getName());
	
	private final Datastore datastore = (Datastore)DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

    public UpdateResource() { } //empty constructor
    
    @POST
    @Path("/{user}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doUpdate(@PathParam("user") String user, UpdateInfo info) {
    	
    	Transaction txn = this.datastore.newTransaction();

        try {
           Key tokenKey = datastore.newKeyFactory()
          		 .addAncestor(PathElement.of("User", user))
          		 .setKind("Token")
          		 .newKey("token");
           Entity tokenEnt = datastore.get(tokenKey);
           Key userKey = datastore.newKeyFactory()
          		 .setKind("User")
          		 .newKey(user);
           Entity userEnt = this.datastore.get(userKey);
          
           
           
           if (this.validUser(userEnt) && this.validToken(info.token, tokenEnt)
          		 && this.validRole("USER", tokenEnt)) {
              
          	String email;
              if(info.data.validEmail())
            	  email = info.data.email;
              else {
            	  email = userEnt.getString("email");            
              }
            	  
              
              String password;
              if(info.data.validPassword() && info.data.validConfirmation() && DigestUtils.sha512Hex(info.data.currentPassword).equals(userEnt.getString("password"))) {
            	  password = info.data.password;
              }  
              else {
            	  password = userEnt.getString("password");
              }
            	     	  
              
              String profile;
              if(info.data.validProfile())
            	  profile = info.data.profile;
              else {
            	  profile = userEnt.getString("profile");
              }
            	  
              
              String landline;
              if(info.data.validLandline())
            	  landline = info.data.landline;
              else {
            	  landline = userEnt.getString("landline");
              }
            	  
              
              String phoneNumber;
              if(info.data.validPhoneNumber())
            	  phoneNumber = info.data.phoneNumber;
              else {
            	  phoneNumber = userEnt.getString("phoneNumber");
              }
            	  
              
              String address;
              if(info.data.validAdress())
            	  address = info.data.address;
              else {
            	  address = userEnt.getString("address");
              }
            	  
              
              String addtitionalAddress;
              if(info.data.validAdditionalAdress())
            	  addtitionalAddress = info.data.additionalAddress;
              else {
            	  addtitionalAddress = userEnt.getString("addtitionalAddress");
              }
            	  
              
              String location;
              if(info.data.validLocation())
            	  location = info.data.location;
              else {
            	  location = userEnt.getString("location");
              }
            	  
              		
              userEnt = Entity.newBuilder(userKey)
              		.set("password", DigestUtils.sha512Hex(password))
              		.set("email", email)
              		.set("role", userEnt.getString("role"))
              		.set("state", userEnt.getString("state"))
            		.set("profile", profile)
            		.set("landline", landline)
            		.set("phoneNumber", phoneNumber)
            		.set("address", address)
            		.set("addtitionalAddress", addtitionalAddress)
            		.set("location", location)
            		.set("user_creation_time", Timestamp.now())
  					.build();
              
              txn.put(userEnt);
              txn.commit();
              return Response.ok().build();
           }
           return Response.status(Status.FORBIDDEN).build();
        
        } finally {
           if (txn.isActive()) {
              txn.rollback();
           }
        }
    	
    }
    
    
    
    
    @POST
    @Path("/{user}/{role}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doUpdateRole(@PathParam("user") String user, @PathParam("role") String role, AuthToken token) {
    	
    	Transaction txn = this.datastore.newTransaction();

        try {
           Key tokenKey = datastore.newKeyFactory()
          		 .addAncestor(PathElement.of("User", token.user))
          		 .setKind("Token")
          		 .newKey("token");
           Entity userUpdatingToken = datastore.get(tokenKey);
           Key userKey = datastore.newKeyFactory()
          		 .setKind("User")
          		 .newKey(user);
           Entity userToUpdate = this.datastore.get(userKey);
          
           
           
           if (this.validUser(userToUpdate) && this.validToken(token, userUpdatingToken)) {
    	
        	   if (this.validRole("SU", userUpdatingToken) && (role.equals("GBO") || role.equals("GA"))) {
        		   userToUpdate = Entity.newBuilder(userKey)
                     	.set("password", userToUpdate.getString("password"))
                     	.set("email", userToUpdate.getString("email"))
                     	.set("role", role)
                     	.set("state", userToUpdate.getString("state"))
                   		.set("profile", userToUpdate.getString("profile"))
                   		.set("landline", userToUpdate.getString("landline"))
                   		.set("phoneNumber", userToUpdate.getString("phoneNumber"))
                   		.set("address", userToUpdate.getString("address"))
                   		.set("addtitionalAddress", userToUpdate.getString("addtitionalAddress"))
                   		.set("location", userToUpdate.getString("location"))
                   		.set("user_creation_time", Timestamp.now())
         					.build();
                     
                     txn.put(userToUpdate);
                     txn.commit();
        	   }
        	   
               if (this.validRole("GA", userUpdatingToken) && role.equals("GBO")) {
            	   userToUpdate = Entity.newBuilder(userKey)
                        	.set("password", userToUpdate.getString("password"))
                        	.set("email", userToUpdate.getString("email"))
                        	.set("role", role)
                        	.set("state", userToUpdate.getString("state"))
                      		.set("profile", userToUpdate.getString("profile"))
                      		.set("landline", userToUpdate.getString("landline"))
                      		.set("phoneNumber", userToUpdate.getString("phoneNumber"))
                      		.set("address", userToUpdate.getString("address"))
                      		.set("addtitionalAddress", userToUpdate.getString("addtitionalAddress"))
                      		.set("location", userToUpdate.getString("location"))
                      		.set("user_creation_time", Timestamp.now())
            					.build();
                        
                        txn.put(userToUpdate);
                        txn.commit();
        	   }
               return Response.ok().build();
           }
           return Response.status(Status.FORBIDDEN).build();
           
        } finally {
            if (txn.isActive()) {
                txn.rollback();
             }
          }
    	
    	
    }
    
    
    
    @POST
    @Path("/{user}/state")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doUpdateState(@PathParam("user") String user, AuthToken token) {
    	
    	Transaction txn = this.datastore.newTransaction();

        try {
           Key tokenKey = datastore.newKeyFactory()
          		 .addAncestor(PathElement.of("User", token.user))
          		 .setKind("Token")
          		 .newKey("token");
           Entity userUpdatingToken = datastore.get(tokenKey);
           Key userKey = datastore.newKeyFactory()
          		 .setKind("User")
          		 .newKey(user);
           Entity userToUpdate = this.datastore.get(userKey);
          
           
           
           if (this.validUser(userToUpdate) && this.validToken(token, userUpdatingToken)) {
    	
        	   if ((this.validRole("SU", userUpdatingToken) || this.validRole("GA", userUpdatingToken) || this.validRole("GBO", userUpdatingToken)) 
        			   && userToUpdate.getString("role").equals("USER")) {
        		   
        		   String state = userToUpdate.getString("state");
        		   if(state.equals("ENABLED"))
        			   state = "DISABLED";
        		   else
        			   state = "ENABLED";
        		   
        		   userToUpdate = Entity.newBuilder(userKey)
                     	.set("password", userToUpdate.getString("password"))
                     	.set("email", userToUpdate.getString("email"))
                     	.set("role", userToUpdate.getString("role"))
                     	.set("state", state)
                   		.set("profile", userToUpdate.getString("profile"))
                   		.set("landline", userToUpdate.getString("landline"))
                   		.set("phoneNumber", userToUpdate.getString("phoneNumber"))
                   		.set("address", userToUpdate.getString("address"))
                   		.set("addtitionalAddress", userToUpdate.getString("addtitionalAddress"))
                   		.set("location", userToUpdate.getString("location"))
                   		.set("user_creation_time", Timestamp.now())
         					.build();
                     
                     txn.put(userToUpdate);
                     txn.commit();
        	   }
        	   
               if ((this.validRole("GA", userUpdatingToken) || this.validRole("SU", userUpdatingToken)) 
            		   && userToUpdate.getString("role").equals("GBO")) {
            	   
            	   String state = userToUpdate.getString("state");
        		   if(state.equals("ENABLED"))
        			   state = "DISABLED";
        		   else
        			   state = "ENABLED";
        		   
        		   userToUpdate = Entity.newBuilder(userKey)
                     	.set("password", userToUpdate.getString("password"))
                     	.set("email", userToUpdate.getString("email"))
                     	.set("role", userToUpdate.getString("role"))
                     	.set("state", state)
                   		.set("profile", userToUpdate.getString("profile"))
                   		.set("landline", userToUpdate.getString("landline"))
                   		.set("phoneNumber", userToUpdate.getString("phoneNumber"))
                   		.set("address", userToUpdate.getString("address"))
                   		.set("addtitionalAddress", userToUpdate.getString("addtitionalAddress"))
                   		.set("location", userToUpdate.getString("location"))
                   		.set("user_creation_time", Timestamp.now())
         					.build();
                        
                        txn.put(userToUpdate);
                        txn.commit();
        	   }
               
               
               if (this.validRole("SU", userUpdatingToken) && userToUpdate.getString("role").equals("GA")) {
            	   
            	   String state = userToUpdate.getString("state");
        		   if(state.equals("ENABLED"))
        			   state = "DISABLED";
        		   else
        			   state = "ENABLED";
        		   
        		   userToUpdate = Entity.newBuilder(userKey)
                     	.set("password", userToUpdate.getString("password"))
                     	.set("email", userToUpdate.getString("email"))
                     	.set("role", userToUpdate.getString("role"))
                     	.set("state", state)
                   		.set("profile", userToUpdate.getString("profile"))
                   		.set("landline", userToUpdate.getString("landline"))
                   		.set("phoneNumber", userToUpdate.getString("phoneNumber"))
                   		.set("address", userToUpdate.getString("address"))
                   		.set("addtitionalAddress", userToUpdate.getString("addtitionalAddress"))
                   		.set("location", userToUpdate.getString("location"))
                   		.set("user_creation_time", Timestamp.now())
         					.build();
                        
                        txn.put(userToUpdate);
                        txn.commit();
        	   }
               
               return Response.ok().build();
           }
           return Response.status(Status.FORBIDDEN).build();
           
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
    
    private boolean validUser(Entity user) {
    	return user != null;
    }
    
    private boolean validRole(String role, Entity tokenEnt) {
    	return tokenEnt.getString("role").equals(role);
    }
    
    
}

