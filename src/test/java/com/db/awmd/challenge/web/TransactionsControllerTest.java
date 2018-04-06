package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransactionsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionsControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private TransactionsService transactionsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

        accountsService.getAccountsRepository().clearAccounts();
        transactionsService.getTransactionsRepository().clearTransaction();

        initAccounts();
    }

    @Test
    public void initiateTransaction() throws Exception {
        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"receiverId\":\"ACC-2\",\"amount\":15.0}"))
                .andExpect(status().isCreated());

        assertAccount(accountsService.getAccount("ACC-1"), "ACC-1", new BigDecimal(85));
        assertAccount(accountsService.getAccount("ACC-2"), "ACC-2", new BigDecimal(115));
    }

    @Test
    public void transferAmountToSameAccount() throws Exception {
        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"receiverId\":\"ACC-1\",\"amount\":15.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void initiateTransactionWithNoSenderId() throws Exception {
        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"receiverId\":\"ACC-1\",\"amount\":15.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void initiateTransactionWithNoReceiverId() throws Exception {
        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"amount\":15.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void initiateTransactionWithNoAmount() throws Exception {
        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"receiverId\":\"ACC-2\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void initiateTransactionWithNegativeAmount() throws Exception {
        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"receiverId\":\"ACC-2\",\"amount\":-15.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void initiateTransactionWithNotEnoughBalanceInSendersAccount() throws Exception {
        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"receiverId\":\"ACC-2\",\"amount\":50.0}"))
                .andExpect(status().isCreated());
        assertAccount(accountsService.getAccount("ACC-1"), "ACC-1", new BigDecimal(50));
        assertAccount(accountsService.getAccount("ACC-2"), "ACC-2", new BigDecimal(150));

        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"receiverId\":\"ACC-2\",\"amount\":50.0}"))
                .andExpect(status().isCreated());
        assertAccount(accountsService.getAccount("ACC-1"), "ACC-1", new BigDecimal(0));
        assertAccount(accountsService.getAccount("ACC-2"), "ACC-2", new BigDecimal(200));

        this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderId\":\"ACC-1\",\"receiverId\":\"ACC-2\",\"amount\":50.0}"))
                .andExpect(status().isBadRequest());
    }


    private void initAccounts() {
        accountsService.createAccount(new Account("ACC-1", new BigDecimal(100)));
        accountsService.createAccount(new Account("ACC-2", new BigDecimal(100)));
    }

    private void assertAccount(final Account account, final String id, final BigDecimal balance) {
        assertThat(account.getAccountId()).isEqualTo(id);
        assertThat(account.getBalance()).isEqualByComparingTo(balance);
    }

}
