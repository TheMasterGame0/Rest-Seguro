package org.acme;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cliente")
@ApplicationScoped
public class Cliente {

    private String url = "http://localhost:8080/server";

    @GET()
    @Path("/get/{body}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getData(@PathParam("body") String body) {
    try{
        System.out.println("Iniciado!");
        // Carrega a chave e o certificado
        PrivateKey privateKey = CryptoUtils.loadPrivateKey("changeit", "client-key", "changeit");
        X509Certificate certificate = (X509Certificate) CryptoUtils.loadCertificate("client-key", "changeit");

        System.out.println("Chave e certificado carregados!");

        // Por ser um get nao tem corpo, mas sera utilizado as informacoes disponiveis
        byte[] bodyBytes = ("GET - /server/get/" + body).getBytes(StandardCharsets.UTF_8);

        // Gera a assinatura e o certificado
        String signature = CryptoUtils.signData(bodyBytes, privateKey);
        String certBase64 = Base64.getEncoder().encodeToString(certificate.getEncoded());

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            System.out.println("Montando requisição");
            // Monta a requisicao
            String urlCompleta = url + "/get/" + body;
            HttpGet get = new HttpGet(urlCompleta);
            get.setHeader("Content-Type", "application/json");
            get.setHeader("X-Signature", signature);
            get.setHeader("X-Certificate", certBase64);

            // Executa a requisicao e trata a resposta
            try (CloseableHttpResponse response = client.execute(get)) {
                System.out.println("Assinatura: " + signature);
                System.out.println("Certificado: " + certBase64);

                System.out.println("GET Status: " + response.getCode());
                System.out.println("GET Response: " + new String(response.getEntity().getContent().readAllBytes()));

                return Response.status(response.getCode()).build();
            }
        } catch (Exception e) {
            System.out.println(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    } catch (Exception e) {
        System.out.println(e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    }

    // @POST()
    // @Produces(MediaType.TEXT_PLAIN)
    // public String postData() {
    // }

    // comando utilizado para geracao das chaves
//     keytool -genkeypair \
//   -alias client-key \
//   -keyalg RSA \
//   -keysize 2048 \
//   -keystore client-keystore.jks \
//   -storepass changeit \       <- senha da chave e da store
//   -validity 365 \
//   -dname "CN=Client, OU=Dev, O=MyOrg, L=City, ST=State, C=BR"


    // private void sendSignedPost(String url, String jsonBody) throws Exception {
    //     byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

    //     String signature = CryptoUtils.signData(bodyBytes, privateKey);
    //     String certBase64 = CryptoUtils.encodeCertificate(certificate);

    //     try (CloseableHttpClient client = HttpClients.createDefault()) {
    //         HttpPost post = new HttpPost(url);
    //         post.setHeader("Content-Type", "application/json");
    //         post.setHeader("X-Signature", signature);
    //         post.setHeader("X-Certificate", certBase64);
    //         post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

    //         try (CloseableHttpResponse response = client.execute(post)) {
    //             System.out.println("POST Status: " + response.getCode());
    //             System.out.println("POST Response: " + new String(response.getEntity().getContent().readAllBytes()));
    //         }
    //     }
    // }

}
 