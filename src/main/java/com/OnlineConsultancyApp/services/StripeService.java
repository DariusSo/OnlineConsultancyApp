package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Consultant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class StripeService {

    @Autowired
    AppointmentService appointmentService;
    @Autowired
    ConsultantService consultantService;

//    @Value("${stripe.api.key}")
//    private String stripeApiKey;


//    @PostConstruct
//    public void init() {
//        Stripe.apiKey = stripeApiKey;
//    }

    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .build();

        return PaymentIntent.create(params);
    }

    public Session createCheckoutSession(Appointment appointment, String token) throws Exception {

        Stripe.apiKey = System.getenv("STRIPE_API");

        UUID uuid = UUID.randomUUID();
        appointment.setUuid(String.valueOf(uuid));
        appointment.setPaid(false);
        appointment.setAccepted(false);

        Consultant consultant = consultantService.getConsultantById(appointment.getConsultantId());
        BigDecimal amount = consultant.getHourlyRate();
        appointment.setPrice(amount);
        String paymentTitle = "Appointment with " +
                consultant.getFirstName() +
                " " +
                consultant.getLastName();
        long expiresAt = (System.currentTimeMillis() / 1000) + 1800;
        appointmentService.addAppointment(appointment, token);
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/redirect?uuid=" + uuid)
                .setCancelUrl("http://localhost:7777/index.html")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount((long) Double.parseDouble(String.valueOf(appointment.getPrice())) * 100)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(paymentTitle)
                                        .build())
                                .build())
                        .build())
                .putExtraParam("expires_at", expiresAt)
                .build();
        Session session = Session.create(params);
        //rs.addSessionId(session.getId(), userId, eventId);
        return session;
    }

    public boolean setAppointmentPaymentStatusTrue(UUID uuid){
        try{
            appointmentService.updatePaymentStatus(uuid);
            return true;
        }catch (Exception e){

        }
        return false;
    }
}
