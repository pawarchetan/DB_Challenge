package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Transaction {

    private Integer transactionId;

    @NotNull
    @NotEmpty
    private String senderId;

    @NotNull
    @NotEmpty
    private String receiverId;

    @NotNull
    @Min(value = 0, message = "Amount must be positive.")
    private BigDecimal amount;

    @JsonCreator
    public Transaction(final @JsonProperty("sender") String sender,
                       final @JsonProperty("receiver") String receiver,
                       final @JsonProperty("amount") BigDecimal amount) {
        this.senderId = sender;
        this.receiverId = receiver;
        this.amount = amount;
        this.transactionId = null;
    }
}
