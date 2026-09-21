package com.Kalakriti.Kalakriti.controller;


import com.Kalakriti.Kalakriti.entity.Contact;
import com.Kalakriti.Kalakriti.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:5173") // allow React frontend
public class ContactController {


    @Autowired
    private ContactRepository contactRepository;

    @PostMapping
    public Contact saveMessage(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }
}
