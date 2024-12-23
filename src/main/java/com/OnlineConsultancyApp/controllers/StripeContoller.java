package com.OnlineConsultancyApp.controllers;


import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.services.StripeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class StripeContoller {

    @Autowired
    StripeService stripeService;


    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Appointment appointment,
                                                                    @RequestHeader("Authorization") String jwtToken) {
        try {
            Session session = stripeService.createCheckoutSession(appointment, jwtToken);
            Map<String, String> responseData = new HashMap<>();
            responseData.put("id", session.getId());
            return ResponseEntity.ok(responseData);
        } catch (StripeException | SQLException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/redirect")
    public RedirectView redirect(@RequestParam UUID uuid){
        return (stripeService.setAppointmentPaymentStatusTrue(uuid)) ?
                new RedirectView("http://localhost:5173/thanks") : // status update successful
                new RedirectView("http://localhost:5173/error"); // status update failed
    }
}
