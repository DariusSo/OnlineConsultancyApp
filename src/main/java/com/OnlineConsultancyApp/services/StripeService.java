package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.enums.Roles;
import com.OnlineConsultancyApp.models.Appointment;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.security.JwtDecoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.jsonwebtoken.Claims;
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
    @Autowired
    AuthService authService;

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
        appointmentService.addStripeSessionId(session.getId(), uuid);
        return session;
    }

    public void createRefund(String token, long appointmentId) throws SQLException, StripeException, JsonProcessingException {
        authService.authenticateRole(token);
        Stripe.apiKey = System.getenv("STRIPE_API");

        String sessionId = appointmentService.getStripeSessionId(appointmentId);
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        Consultant consultant = consultantService.getConsultantById(appointment.getConsultantId());
        BigDecimal price = appointment.getPrice();

        Session session = Session.retrieve(sessionId);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());

        Charge charge = Charge.retrieve(paymentIntent.getLatestCharge());

        RefundCreateParams params =
                RefundCreateParams.builder().setCharge(String.valueOf(charge.getId())).setAmount((long) price.doubleValue() * 100).build();

        Refund refund = Refund.create(params);
        appointmentService.deleteAppointment(appointmentId, consultantService.getDates(consultant.getId()), appointment.getTimeAndDate(), consultant.getId());
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
