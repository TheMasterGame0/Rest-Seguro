// package org.acme;

// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.security.PublicKey;
// import java.security.Signature;
// import java.security.cert.CertificateFactory;
// import java.security.cert.X509Certificate;
// import java.util.Base64;

// import jakarta.ws.rs.container.ContainerRequestContext;
// import jakarta.ws.rs.container.ContainerRequestFilter;
// import jakarta.ws.rs.core.Response;
// import jakarta.ws.rs.ext.Provider;

// @Provider
// public class SignatureFilter implements ContainerRequestFilter {

//     @Override
//     public void filter(ContainerRequestContext requestContext) throws IOException {
//         String signatureBase64 = requestContext.getHeaderString("X-Signature");
//         String certBase64 = requestContext.getHeaderString("X-Certificate");

//         if (signatureBase64 == null || certBase64 == null) {
//             requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
//             return;
//         }

//         byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
//         byte[] certBytes = Base64.getDecoder().decode(certBase64);

//         try {
//             CertificateFactory cf = CertificateFactory.getInstance("X.509");
//             X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));
//             PublicKey publicKey = cert.getPublicKey();

//             // Lê o corpo da requisição novamente
//             InputStream entityStream = requestContext.getEntityStream();
//             byte[] entityBytes = entityStream.readAllBytes();
//             requestContext.setEntityStream(new ByteArrayInputStream(entityBytes)); // reinjeta para o JAX-RS ler depois

//             Signature sig = Signature.getInstance("SHA256withRSA");
//             sig.initVerify(publicKey);
//             sig.update(entityBytes);

//             boolean isValid = sig.verify(signatureBytes);

//             if (!isValid) {
//                 requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Assinatura inválida").build());
//             }

//             // (Opcional) verificar validade, cadeia, CRL, etc.
//             cert.checkValidity();

//         } catch (Exception e) {
//             requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Erro na verificação da assinatura").build());
//         }
//     }
// }
