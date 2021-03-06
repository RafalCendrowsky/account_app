package com.rafalcendrowski.AccountApplication.models;

import com.rafalcendrowski.AccountApplication.controllers.PaymentsController;
import com.rafalcendrowski.AccountApplication.controllers.UserController;
import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<Payment, EntityModel<PaymentDto>> {
    @Override
    public EntityModel<PaymentDto> toModel(Payment payment) {
        return EntityModel.of(PaymentDto.of(payment),
                linkTo(methodOn(PaymentsController.class).getPayment(payment.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUser(payment.getEmployee().getId())).withRel("user"),
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments")
        );
    }
}
