 Following changes is done for the same.

1 Added TransferRequest DTO in domain pkg.
2 Updated Account Service with this new method for money transfer between accounts.
public void transferMoney(TransferRequest transferRequest)

3 Updated the controller and Expose an endpoint to initiate a transfer.
4 Mock the NotificationService and test the transfer functionality

5 The use of ReentrantLock ensures thread-safe operations. Only one thread can execute the critical
section at a time.

6 for notification interface is implemented just for mock testing.

You can run the test cases for both sucessful transfer and Insufficient Fund but for Insufficientfund test case (testInsufficientFunds()) you have to change value in setUp() change accountFrom value from 600 to 50 which 
is less than 100 so it will satify the condition and give you sucessful test case.
