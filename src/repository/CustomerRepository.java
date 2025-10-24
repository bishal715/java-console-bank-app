package repository;

import domain.Account;
import domain.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CustomerRepository {
    private final Map<String, Customer> customersById = new HashMap<>();

    public List<Customer> findAll() {
        return new ArrayList<>(customersById.values());
    }

    public void save(Customer c) {
        customersById.put(c.getCustomerId(), c);
    }
}
