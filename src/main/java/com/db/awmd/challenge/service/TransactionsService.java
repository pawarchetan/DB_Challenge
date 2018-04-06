package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.exception.SenderAndReceiverSameException;
import com.db.awmd.challenge.repository.TransactionsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

import static com.db.awmd.challenge.ApplicationConstants.*;

@Service
@Slf4j
public class TransactionsService {

    @Getter
    private final NotificationService notificationService;

    @Getter
    private final AccountsService accountsService;

    @Getter
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public TransactionsService(final NotificationService notificationService,
                               final AccountsService accountsService,
                               final TransactionsRepository transactionsRepository) {
        this.notificationService = notificationService;
        this.accountsService = accountsService;
        this.transactionsRepository = transactionsRepository;
    }

    public void initiateTransaction(final Transaction transaction) {

        final Account senderAccount = accountsService.getAccount(transaction.getSenderId());
        final Account receiverAccount = accountsService.getAccount(transaction.getReceiverId());

        if (senderAccount == null) {
            throw new AccountNotExistException(String.format(SENDER_ACCOUNT_NOT_EXIST, String.valueOf(transaction.getSenderId())));
        }

        if (receiverAccount == null) {
            throw new AccountNotExistException(String.format(RECEIVER_ACCOUNT_NOT_EXIST, String.valueOf(transaction.getReceiverId())));
        }

        if (transaction.getSenderId().equals(transaction.getReceiverId())) {
            throw new SenderAndReceiverSameException(String.format(SENDER_IS_THE_RECEIVER_MESSAGE,
                    transaction.getSenderId(), transaction.getReceiverId()));
        }

        transferAmount(senderAccount, receiverAccount, transaction);

        transactionsRepository.createTransaction(transaction);
        notificationService.notifyAboutTransfer(senderAccount, String.format(SENDER_NOTIFICATION_MESSAGE,
                transaction.getAmount().toPlainString(), receiverAccount.getAccountId()));
        notificationService.notifyAboutTransfer(receiverAccount, String.format(RECEIVER_NOTIFICATION_MESSAGE,
                transaction.getAmount().toPlainString(), senderAccount.getAccountId()));
    }

    private void transferAmount(Account senderAccount, Account receiverAccount, final Transaction transaction) {
        final ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            if (senderAccount.getBalance().doubleValue() < transaction.getAmount().doubleValue()) {
                throw new InsufficientFundsException(String.format(NOT_ENOUGH_BALANCE,
                        transaction.getSenderId(), transaction.getAmount().toPlainString()));
            }
            updateBalances(senderAccount, receiverAccount, transaction.getAmount());
        } finally {
            lock.unlock();
        }
    }

    private void updateBalances(final Account transferFromAccount, final Account transferToAccount, final BigDecimal amount) {
        transferFromAccount.setBalance(transferFromAccount.getBalance().subtract(amount));
        transferToAccount.setBalance(transferToAccount.getBalance().add(amount));
    }
}
