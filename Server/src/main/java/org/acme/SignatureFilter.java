package org.acme;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SignatureFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("Validando assinatura");
        // Obtem do Header da requisicao a assinatura e o certificado
        String signatureBase64 = requestContext.getHeaderString("X-Signature");
        String certBase64 = requestContext.getHeaderString("X-Certificate");

        // Caso esteja sem algum dos dois, retorna como não autorizado
        if (signatureBase64 == null || certBase64 == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        byte[] certBytes = Base64.getDecoder().decode(certBase64);

        try {
            // Recupera a chave publica do certificado
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));

            // verificar validade, cadeia, CRL, etc.
            cert.checkValidity();
            PublicKey publicKey = cert.getPublicKey();

            String method = requestContext.getMethod();
            String path = requestContext.getUriInfo().getPath();
            byte[] entityBytes = new byte[0];
            if(!method.equals("GET")){
              // Pega o corpo da requisicao
              InputStream entityStream = requestContext.getEntityStream();
              entityBytes = entityStream.readAllBytes();
              requestContext.setEntityStream(new ByteArrayInputStream(entityBytes)); // reinjeta para ser lido depois
            }else{
              entityBytes = (method + " - "+path).getBytes();
            }

            // Valida a assinatura do certificado com chave pública e a mensagem recebida
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(entityBytes);
            boolean isValid = sig.verify(signatureBytes);

            if (!isValid) {
              // Retorna 403 se a assinatura for inválida
              requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Assinatura inválida").build());
            }
            System.out.println("Assinatura válida");

        } catch(CertificateExpiredException e) {
          requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Certificado expirado").build());
        } catch (Exception e) {
          requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Erro na verificação da assinatura").build());
        }
    }
}
