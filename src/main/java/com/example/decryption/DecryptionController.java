package com.example.decryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DecryptionController {

    private final DecryptionService decryptionService;

    // Inject the private key from application.properties.
    @Value("${wasabi.private.key}")
    private String wasabiPrivateKey;

    public DecryptionController(DecryptionService decryptionService) {
        this.decryptionService = decryptionService;
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody DecryptRequest request) {
        try {
            // Use the private key loaded from configuration.
            return decryptionService.decryptPrivateKey(request.getData(), wasabiPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during decryption: " + e.getMessage();
        }
    }
}
