package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TransactionsRepositoryInMemory implements TransactionsRepository {

    private static AtomicInteger transactionId = new AtomicInteger();
    private final Map<Integer, Transaction> transactions = new ConcurrentHashMap<>();

    @Override
    public void createTransaction(final Transaction transaction) {
        transaction.setTransactionId(transactionId.incrementAndGet());
        transactions.put(transaction.getTransactionId(), transaction);
    }

    @Override
    public void clearTransaction() {
        transactions.clear();
    }
}
