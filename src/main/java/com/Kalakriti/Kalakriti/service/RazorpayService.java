package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.entity.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class RazorpayService {

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws RazorpayException {
        razorpayClient = new RazorpayClient(key, secret);
    }

    public com.razorpay.Order createRazorpayOrder(Order order)
            throws RazorpayException {

        JSONObject options = new JSONObject();

        options.put(
                "amount",
                Math.round(order.getTotalPrice() * 100)
        );

        options.put("currency", "INR");
        options.put("receipt", "order_" + order.getId());

        return razorpayClient.orders.create(options);
    }

    public boolean verifySignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) throws RazorpayException {

        try {
            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            return Utils.verifyPaymentSignature(attributes, secret);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean verifyPayment(
            String orderId,
            String paymentId,
            String signature
    ) throws Exception {

        String data = orderId + "|" + paymentId;

        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        sha256Hmac.init(secretKeySpec);

        byte[] hash = sha256Hmac.doFinal(
                data.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder generatedSignature = new StringBuilder();

        for (byte b : hash) {
            generatedSignature.append(String.format("%02x", b));
        }

        return generatedSignature.toString().equals(signature);
    }
}