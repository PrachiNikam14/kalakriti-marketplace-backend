package com.Kalakriti.Kalakriti.service.impl;

import com.Kalakriti.Kalakriti.service.SmsService;
import org.springframework.stereotype.Service;

@Service
public class MockSmsService implements SmsService {

    @Override
    public void sendSms(String phoneNumber, String message) {

        // For now just simulate SMS
        System.out.println("📱 Sending SMS to: " + phoneNumber);
        System.out.println("📩 Message: " + message);
        System.out.println("✅ SMS sent successfully (mock)");
    }
}