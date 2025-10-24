package service;

import domain.Account;
import domain.Transaction;

import java.lang.reflect.AccessFlag;
import java.util.List;
import java.util.Map;

public interface BankService {
    String openAccount(String name, String email, String accountType);
    List<Account> listAccounts();

    void deposit(String accountNumber, Double amount, String note);

    void withdraw(String accountNumber, Double amount, String note);

    void transfer(String from, String to, Double amount, String note);

    List<Transaction> getStatement(String accountNumber);

    List<Account> searchAccountsByCustomerName(String q);
}
