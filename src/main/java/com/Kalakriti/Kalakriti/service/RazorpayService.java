package com.Kalakriti.Kalakriti.service;

import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class RazorpayService {

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    public Order createRazorpayOrder(double amount) throws RazorpayException {

        RazorpayClient client = new RazorpayClient(key, secret);
        System.out.println("KEY: " + key);
        System.out.println("SECRET: " + secret);

        JSONObject options = new JSONObject();
        options.put("amount", (int)(amount * 100)); // amount in paise
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        return client.orders.create(options);
    }

    public boolean verifySignature(String orderId,
                                   String paymentId,
                                   String signature) throws Exception {

        String payload = orderId + "|" + paymentId;

        String generatedSignature = hmacSHA256(payload, secret);

        return generatedSignature.equalsIgnoreCase(signature);
    }

    private String hmacSHA256(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey =
                new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        mac.init(secretKey);

        byte[] hash = mac.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(hash);
    }
}