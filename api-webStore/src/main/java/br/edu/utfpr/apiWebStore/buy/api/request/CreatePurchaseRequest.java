package br.edu.utfpr.apiWebStore.buy.api.request;

import br.edu.utfpr.apiWebStore.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CreatePurchaseRequest(
        @NotNull(message = "Os dados do comprador são obrigatórios.")
        @Valid CreateBuyerRequest buyer,

        @NotNull(message = "O endereço de entrega é obrigatório.")
        @Valid DeliveryAddressRequest deliveryAddress,

        @NotNull(message = "O valor total é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero.")
        @Digits(integer = 17, fraction = 2)
        BigDecimal totalAmount,

        @NotEmpty(message = "A compra deve possuir pelo menos um produto.")
        List<@Valid CreatePurchaseItemRequest> purchaseItems

) {

}
