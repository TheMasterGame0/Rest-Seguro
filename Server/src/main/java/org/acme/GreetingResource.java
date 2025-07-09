package org.acme;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/server")
@ApplicationScoped
public class GreetingResource {

    @GET()
    @PermitAll
    @Path("get/{body}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("body") String body) {
        String helloReply = String.format("Hello! textoPedido: %s", body);
        return helloReply;
    }

    // @GET()
    // @Path("roles-allowed") 
    // @RolesAllowed({"Subscriber"}) 
    // @Produces(MediaType.TEXT_PLAIN)
    // public String helloRolesAllowed(@Context SecurityContext ctx) {
    //     Principal caller =  ctx.getUserPrincipal();
    //     String name = caller == null ? "anonymous" : caller.getName();
    //     String helloReply = String.format("hello + %s, isSecure: %s, authScheme: %s", name, ctx.isSecure(), ctx.getAuthenticationScheme());
    //     return helloReply;
    // }
}
 