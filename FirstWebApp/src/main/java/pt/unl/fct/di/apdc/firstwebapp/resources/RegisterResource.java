package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
	
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	
	private final Gson g = new Gson();

	Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RegisterResource() { } //empty constructor
    
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doRegistration(LoginData data) {
    	
    	this.registerSU();
    	
    	if (!data.validRegistration()) {
            return Response.status(Status.BAD_REQUEST)
            		.entity("Missing or wrong parameter. Note: Password must have at least one numeric character, one lowercase character, one uppercase character and should be between 8 and 20")
            		.build();
         
         } else {
            Transaction txn = datastore.newTransaction();
         
            try {
               Key userKey = datastore.newKeyFactory()
               		.setKind("User")
               		.newKey(data.username);
              
               Entity user = this.datastore.get(userKey);
         
               if (user == null) {
                  user = Entity.newBuilder(userKey)
                	   .set("username", data.username)
                	   .set("email", data.email)
               		   .set("password", DigestUtils.sha512Hex(data.password))
               		   .set("role", "USER")       
               		   .set("state", "ENABLED")    
               		   .set("profile", data.profile)
               		   .set("landline", data.landline)
               		   .set("phoneNumber", data.phoneNumber)
               		   .set("address", data.address)
               		   .set("addtitionalAddress", data.additionalAddress)
               		   .set("location", data.location)
               		   .set("user_creation_time", Timestamp.now())
               		   .build();
                  
                  
                  txn.add(user);
                  txn.commit();
                  LOG.info("User registered " + data.username);
                  return Response.ok("User registered").build();
               }
               return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
            } finally {
               if (txn.isActive()) {
                  txn.rollback();
               }
            }
         }
    }
    
    
    private void registerSU() {
    	Transaction txn = datastore.newTransaction();
    	
    	Key userKey = datastore.newKeyFactory()
           		.setKind("User")
           		.newKey("boostrapUser");
          
           Entity user = this.datastore.get(userKey);
     
           if (user == null) {
              user = Entity.newBuilder(userKey)
            	   .set("username", "boostrapUser")
            	   .set("email", "")
           		   .set("password", DigestUtils.sha512Hex("boostrapUser"))
           		   .set("role", "SU")       
           		   .set("state", "ENABLED")    
           		   .set("profile", "")
           		   .set("landline", "")
           		   .set("phoneNumber", "")
           		   .set("address", "")
           		   .set("addtitionalAddress", "")
           		   .set("location", "")
           		   .set("user_creation_time", Timestamp.now())
           		   .build();
              
              
              txn.add(user);
              txn.commit();
           }
    }
    
    
    
}
    
