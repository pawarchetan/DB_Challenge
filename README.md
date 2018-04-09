# DB_Challenge
DB Java Challenge

## Technology Stack

 - Java8
 - Lombok
 - Junit
 - Mockito
 - Gradle 4.6
 - IDE Used : Intellij
 
 ## How to Build and Run
 
 Verify test:

    gradle test
    
start spring boot application:

    gradle bootRun

 
 ## Architecture / Design
 
  - Transaction is a class created to keep track of transaction between 2 accounts (sender and receiver). To save Transactions in In-Memory and to deal with concurrency, we have used ConcurrentHashMap as a storage.
  - To deal with concurrency we are relying on ReentrantLock which created pseudo transaction. 
  - Exceptions : We have created 3 extra custom exceptions to get details about any abnormal conditions occuring inside application such as Insufficient funds, Account not exist, sender and receiver are same which can not be.
  - Code coverage (95%) : code coverage has been measured using Intellij IDEA in built plugin.
  
## Future Scope : (Getting current code to Production ready code)

  - As we are using In-Memory storage for storing accounts and their transaction, we can replace In-Memory storage with database. 
  - Once we will use database , we can use JPA / Hibernate to perist entitie.
  - we can replace reentrant lock with optimistic locking.
  - we can use Jacoco like plugin to measure code coverage
  - we can use Authorization and Authentication to secure API's.
  - We can use Swagger / Open API specification to document API's.
  - We can use Spring boot actuator to measure health of system.
