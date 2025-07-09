package org.acme;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;

public class CryptoUtils {

    private static final String keystorePath = "client-keystore.jks";   

    public static String signData(byte[] data, PrivateKey privateKey) throws Exception {
        // Inicializa o algoritmo de assinatura
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(privateKey);
        signer.update(data);
        // Obtem a mensagem assinada
        byte[] signature = signer.sign();
        // Retorna a mensagem assinada codificada na Base64
        return Base64.getEncoder().encodeToString(signature);
    }

    public static PrivateKey loadPrivateKey(String storePassword, String alias, String keyPassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");

        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keystore.load(fis, storePassword.toCharArray());
        }

        Key key = keystore.getKey(alias, keyPassword.toCharArray());
        if (!(key instanceof PrivateKey)) {
            throw new IllegalArgumentException("Not a private key for alias: " + alias);
        }

        return (PrivateKey) key;
    }

    public static Certificate loadCertificate(String alias, String storePassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keystore.load(fis, storePassword.toCharArray());
        }

        Certificate cert = keystore.getCertificate(alias);
        return cert;
    }

}
