package com.example.decryption;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

@Service
public class DecryptionService {

    private static final String KEY_ALGORITHM = "RSA";
    // For a 2048-bit key using PKCS#1 padding, the ciphertext size is typically 256 bytes.
    // If the ciphertext is less than or equal to that, we decrypt in one block.
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * Strips PEM headers, footers, and whitespace from a PEM-formatted key string.
     */
    private String stripPEMHeaders(String pem) {
        return pem.replaceAll("-----BEGIN (.*)-----", "")
                  .replaceAll("-----END (.*)-----", "")
                  .replaceAll("\\s", "");
    }

    public String decryptPrivateKey(String data, String privateKey) throws Exception {
        if (data.contains("%")) {
            data = URLDecoder.decode(data, "UTF-8");
        }
        byte[] rs = Base64.getDecoder().decode(data);
        byte[] decryptedBytes = decryptByPrivateKey(rs, privateKey);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
    // If the key contains PEM headers, strip them out.
    Security.addProvider(new BouncyCastleProvider());
    if (privateKey.contains("-----BEGIN")) {
        privateKey = stripPEMHeaders(privateKey);
    }
    // Decode the private key (assumed to be a raw Base64 string in PKCS#8 format)
    byte[] keyBytes = Base64.getDecoder().decode(privateKey);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);

    // Create a Cipher using PKCS#1 padding explicitly.
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
    cipher.init(Cipher.DECRYPT_MODE, privateK);

    // Decrypt the entire ciphertext at once.
    return cipher.doFinal(encryptedData);
}

}
