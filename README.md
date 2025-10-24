# Console Bank Application

A simple, text-based banking application built in Java. This project simulates basic banking operations like opening accounts, depositing, withdrawing, and transferring funds, all through a command-line interface.

This project runs entirely in the console and uses in-memory collections to store data (no database). It is designed to demonstrate core Java principles, separation of concerns (domain, repository, service), and custom exception handling.

## 🚀 Features

* **Open Account:** Create a new customer and a (SAVINGS/CURRENT) account.
* **Deposit:** Add funds to an existing account.
* **Withdraw:** Remove funds from an existing account (with insufficient funds check).
* **Transfer:** Move funds between two different accounts.
* **View Statement:** See a full, sorted transaction history for a single account.
* **List Accounts:** Display all active accounts in the bank.
* **Search Accounts:** Find accounts based on a customer's name.

## 🛠️ Tech Stack

* **Java 15+:** Uses modern Java features like Text Blocks (`"""`) and `switch` expressions.
* **Core Java Libraries:**
    * `java.util.Scanner` for console input.
    * `java.util.List`, `java.util.ArrayList`, `java.util.Comparator` for in-memory storage and sorting.
    * `java.util.UUID` for generating unique customer IDs.
    * `java.time.LocalDateTime` for transaction timestamps.
    * `java.util.stream.Stream` for data processing.

## 📂 Project Structure

The project follows a clear, layered architecture to separate responsibilities.

```plaintext
bank-app/
└── src/
    ├── app/
    │   └── Main.java       # Entry point, handles all console I/O
    ├── domain/
    │   ├── Account.java    # Represents a bank account
    │   ├── Customer.java   # Represents an account holder
    │   ├── Transaction.java # Records a single transaction
    │   └── Type.java       # Enum for transaction types (DEPOSIT, WITHDRAW, etc.)
    ├── exceptions/
    │   ├── AccountNotFoundException.java
    │   ├── InsufficientFundsException.java
    │   └── ValidationException.java
    ├── repository/
    │   ├── AccountRepository.java   # Manages in-memory storage for Accounts
    │   ├── CustomerRepository.java  # Manages in-memory storage for Customers
    │   └── TransactionRepository.java # Manages in-memory storage for Transactions
    ├── service/
    │   ├── BankService.java      # Interface defining business logic
    │   └── impl/
    │       └── BankServiceImpl.java  # Implementation of the business logic
    └── util/
        └── Validation.java     # (Functional) Interface for data validation
