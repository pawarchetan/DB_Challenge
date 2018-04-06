package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.service.TransactionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transactions")
@Slf4j
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(final TransactionsService transactionService) {
        this.transactionsService = transactionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> initiateTransaction(@RequestBody @Valid Transaction transaction) {
        log.info("Making a transaction {}", transaction);

        try {
            this.transactionsService.initiateTransaction(transaction);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
