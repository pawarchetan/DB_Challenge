package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.exception.SenderAndReceiverSameException;
import com.db.awmd.challenge.repository.TransactionsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionsServiceTest {

    @InjectMocks
    private TransactionsService transactionService;

    @Mock
    private AccountsService accountsService;

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private NotificationService notificationService;

    private Account mockAccount1;
    private Account mockAccount2;

    @Before
    public void setUp() {
        mockAccount1 = new Account("ACC-1", new BigDecimal(1000));
        mockAccount2 = new Account("ACC-2", new BigDecimal(5000));

        when(accountsService.getAccount("ACC-1")).thenReturn(mockAccount1);
        when(accountsService.getAccount("ACC-2")).thenReturn(mockAccount2);
    }

    @Test
    public void transferAmount() throws Exception {
        final Transaction transaction = new Transaction("ACC-1", "ACC-2", new BigDecimal(150));

        transactionService.initiateTransaction(transaction);

        verify(transactionsRepository).createTransaction(transaction);
        verify(notificationService).notifyAboutTransfer(mockAccount1, "Sent 150 to ACC-2");
        verify(notificationService).notifyAboutTransfer(mockAccount2, "Received 150 from ACC-1");
    }

    @Test(expected = AccountNotExistException.class)
    public void transferAmountToIncorrectReceiverAccount() {
        final Transaction transaction = new Transaction("ACC-1", "ACC-3", new BigDecimal(150));

        transactionService.initiateTransaction(transaction);

        verify(transactionsRepository, never()).createTransaction(transaction);
        verify(notificationService, never()).notifyAboutTransfer(any(), anyString());
    }

    @Test(expected = AccountNotExistException.class)
    public void transferAmountFromIncorrectSenderAccount() {
        final Transaction transaction = new Transaction("ACC-3", "ACC-2", new BigDecimal(150));

        transactionService.initiateTransaction(transaction);

        verify(transactionsRepository, never()).createTransaction(transaction);
        verify(notificationService, never()).notifyAboutTransfer(any(), anyString());
    }


    @Test(expected = SenderAndReceiverSameException.class)
    public void transferAmountToSameAccount() throws Exception {
        final Transaction transaction = new Transaction("ACC-2", "ACC-2", new BigDecimal(5));

        transactionService.initiateTransaction(transaction);

        verify(transactionsRepository, never()).createTransaction(transaction);
        verify(notificationService, never()).notifyAboutTransfer(any(), anyString());
    }

    @Test(expected = InsufficientFundsException.class)
    public void transferAmountAboveBalance() throws Exception {
        final Transaction transaction = new Transaction("ACC-1", "ACC-2", new BigDecimal(1500));

        transactionService.initiateTransaction(transaction);

        verify(transactionsRepository, never()).createTransaction(transaction);
        verify(notificationService, never()).notifyAboutTransfer(any(), anyString());
    }
}
