package com.db.awmd.challenge;

import com.db.awmd.challenge.service.TransactionsServiceTest;
import com.db.awmd.challenge.web.TransactionsControllerTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AccountsServiceTest.class, TransactionsServiceTest.class,
        AccountsControllerTest.class, TransactionsControllerTest.class
})
@Ignore
public class ApplicationTestSuite {
}
