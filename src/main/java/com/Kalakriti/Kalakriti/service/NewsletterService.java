package com.Kalakriti.Kalakriti.service;

import com.Kalakriti.Kalakriti.entity.Newsletter;
import com.Kalakriti.Kalakriti.repository.NewsletterRepository;
import org.springframework.stereotype.Service;

@Service
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    public String subscribe(String email) {

        String cleanEmail = email.trim().toLowerCase();

        if (newsletterRepository.existsByEmail(cleanEmail)) {
            return "This email is already subscribed";
        }

        Newsletter newsletter = new Newsletter(cleanEmail);
        newsletterRepository.save(newsletter);

        return "You have subscribed successfully";
    }
}