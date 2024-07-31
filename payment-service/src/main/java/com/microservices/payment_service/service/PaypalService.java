package com.microservices.payment_service.service;

import com.microservices.payment_service.dto.OrderDto;
import com.microservices.payment_service.model.PayLogic;
import com.microservices.payment_service.repository.PaymentRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class PaypalService {
    private final APIContext apiContext;

    private final PaymentRepository paymentRepository;

    public Payment createPayment(String orderId,
                                 Double total,
                                 String currency,
                                 String method,
                                 String intent,
                                 String description,
                                 String cancelUrl,
                                 String successUrl) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US, "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactionList);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        Payment createdPayment = payment.create(apiContext);

        return createdPayment;
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }

    @KafkaListener(topics = "order-pay-events", groupId = "pay_order_id")
    public void consumeBookEvent(OrderDto orderDto) {
        System.out.println("Received event for order: " + orderDto);
        String orderId = String.valueOf(orderDto.getId());
        Double total = orderDto.getTotalAmount();
        PayLogic payLogic = new PayLogic();
        payLogic.setTotalAmount(total);
        payLogic.setOrderId(orderId);
        paymentRepository.save(payLogic);
        System.out.println("order id "+orderId);
        System.out.println("total is "+total);
    }
}
