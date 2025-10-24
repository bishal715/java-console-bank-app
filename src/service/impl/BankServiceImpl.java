package service.impl;

import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.ValidationException;
import repository.AccountRepository;
import repository.CustomerRepository;
import repository.TransactionRepository;
import service.BankService;
import util.Validation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BankServiceImpl implements BankService {
    private final AccountRepository accountRepository = new AccountRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();

    private final Validation<String> validateName = name -> {
        if(name == null || name.isBlank()) throw new ValidationException("Name is required :(");
    };
    private final Validation<String> validateEmail = email -> {
        if(email == null || !email.contains("@")) throw new ValidationException("Email is required :(");
    };
    private final Validation<String> validateType = type -> {
        if(type == null || !type.equalsIgnoreCase("savings") || !type.equalsIgnoreCase("current")) throw new ValidationException("Type must be savings or current!");
    };
    private final Validation<Double> validateAmountPositive = amount -> {
        if(amount == null || amount < 0) throw new ValidationException("Amount cannot be negative!");
    };

    @Override
    public String openAccount(String name, String email, String accountType) {
        validateName.validate(name);
        validateEmail.validate(email);
        String customerId = UUID.randomUUID().toString();
        Customer c = new Customer(customerId, name, email);
        customerRepository.save(c);
        String accountNumber = getAccountNumber();
        Account account = new Account(accountNumber, customerId, 0.0, accountType);
        accountRepository.save(account);
        return accountNumber;
    }

    @Override
    public List<Account> listAccounts() {
        return accountRepository.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public void deposit(String accountNumber, Double amount, String note) {
        validateAmountPositive.validate(amount);
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(()->new AccountNotFoundException("Account Not Found: "+ accountNumber));
        account.setBalance(account.getBalance() + amount);
        Transaction transaction = new Transaction(account.getAccountNumber(), amount, UUID.randomUUID().toString(), note, LocalDateTime.now(), Type.DEPOSIT);
        transactionRepository.add(transaction);
    }

    @Override
    public void withdraw(String accountNumber, Double amount, String note) {
        validateAmountPositive.validate(amount);
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(()->new AccountNotFoundException("Account Not Found: "+ accountNumber));
        if (account.getBalance().compareTo(amount) < 0){
            throw new InsufficientFundsException("Insufficient Balance!!");
        }
        account.setBalance(account.getBalance() - amount);
        Transaction transaction = new Transaction(account.getAccountNumber(), amount, UUID.randomUUID().toString(), note, LocalDateTime.now(), Type.WITHDRAW);
        transactionRepository.add(transaction);
    }

    @Override
    public void transfer(String fromAcc, String toAcc, Double amount, String note) {
        validateAmountPositive.validate(amount);
        if (fromAcc.equals(toAcc)){
            throw new ValidationException("Cannot transfer to your own account!!");
        }
        Account from = accountRepository.findByNumber(fromAcc).orElseThrow(()-> new AccountNotFoundException("Account not found!"));
        Account to = accountRepository.findByNumber(toAcc).orElseThrow(()-> new AccountNotFoundException("Account not found!"));
        if (from.getBalance().compareTo(amount) < 0){
            throw new InsufficientFundsException("Insufficient Balance!!");
        }
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        Transaction fromTransaction = new Transaction(from.getAccountNumber(), amount, UUID.randomUUID().toString(), note, LocalDateTime.now(), Type.TRANSFER_OUT);
        transactionRepository.add(fromTransaction);
        Transaction toTransaction = new Transaction(to.getAccountNumber(), amount, UUID.randomUUID().toString(), note, LocalDateTime.now(), Type.TRANSFER_IN);
        transactionRepository.add(toTransaction);
    }

    @Override
    public List<Transaction> getStatement(String accountNumber) {
        return transactionRepository.findByAccount(accountNumber).stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp)).collect(Collectors.toList());
    }

    @Override
    public List<Account> searchAccountsByCustomerName(String q) {
        String query = (q == null) ? "" : q.toLowerCase();
        List<Account> result = new ArrayList<>();
        for (Customer c : customerRepository.findAll()){
            if (c.getName().toLowerCase().contains(query))
                result.addAll(accountRepository.findByCustomerId(c.getCustomerId()));
        }
        result.sort(Comparator.comparing(Account::getAccountNumber));
        return result;
    }

    private String getAccountNumber() {
        return String.format("AC%06d", accountRepository.findAll().size()+1);
    }
}
