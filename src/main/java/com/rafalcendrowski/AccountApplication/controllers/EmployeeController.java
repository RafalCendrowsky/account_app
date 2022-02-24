package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentRepository;
import com.rafalcendrowski.AccountApplication.payment.PaymentService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserRepository;
import com.rafalcendrowski.AccountApplication.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empl")
public class EmployeeController {

    @Autowired
    UserService userService;

    @Autowired
    PaymentService paymentService;

    @GetMapping(value = "/payment", params = {"period"})
    public Map<String, Object> getPayment(@RequestParam String period,
                                          @AuthenticationPrincipal User user) {
        if (!period.matches("(0[1-9]|1[0-2])-\\d\\d\\d\\d")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period");
        }
        Payment payment = paymentService.loadByEmployeeAndPeriod(user, period);
        return getPaymentMap(user, payment);
    }

    private Map<String, Object> getPaymentMap(User user, Payment payment) {
        String dollars = String.valueOf(payment.getSalary()/100L);
        dollars = dollars.isBlank() ? "0 dollar(s)" : dollars.concat(" dollar(s) ");
        String cents = String.valueOf(payment.getSalary()%100L);
        cents = cents.isBlank() ? "0 cent(s)" : cents.concat(" cent(s)");
        return Map.of("name", user.getName(), "lastname", user.getLastname(),
                "period", payment.getPeriod(), "salary", dollars + cents);
    }

    @GetMapping("/payment")
    public List<Map<String, Object>> getPayment(@AuthenticationPrincipal User user) {
        List<Map<String, Object>> payments = new ArrayList<>();
        for(Payment payment: paymentService.loadByEmployee(user)) {
            payments.add(getPaymentMap(user, payment));
        }
        return payments;
    }
}
