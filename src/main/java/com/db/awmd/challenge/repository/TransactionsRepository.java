package com.db.awmd.challenge.repository;


import com.db.awmd.challenge.domain.Transaction;

public interface TransactionsRepository {

    void createTransaction(final Transaction transaction);

    void clearTransaction();
}

