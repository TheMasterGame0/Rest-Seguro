package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/server")
@ApplicationScoped
public class GreetingResource {

    @GET
    @Path("/get/{body}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDataFromServer(@PathParam("body") String body) {
        // Se a requisição chegou aqui, o SignatureFilter a validou com sucesso.
        System.out.println("[Server GET] Requisição recebida e validada para o parâmetro: " + body);
        
        String resposta = "Olá do servidor! O texto pedido foi: " + body;
        
        return Response.ok(resposta).build();
    }

    @POST
    @Path("/data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveData(String jsonData) {
        // Se a requisição chegou aqui, o SignatureFilter já validou o corpo (jsonData)
        System.out.println("[Server POST] Dados JSON recebidos e validados: " + jsonData);

        String responseJson = "{\"status\":\"recebido com sucesso\", \"dados\": " + jsonData + "}";

        return Response.ok(responseJson).build();
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
 