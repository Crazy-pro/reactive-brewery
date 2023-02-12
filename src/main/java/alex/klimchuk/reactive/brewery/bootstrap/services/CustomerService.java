package alex.klimchuk.reactive.brewery.bootstrap.services;

import alex.klimchuk.reactive.brewery.web.model.CustomerDto;

import java.util.UUID;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
public interface CustomerService {

    CustomerDto getCustomerById(UUID customerId);

    CustomerDto saveNewCustomer(CustomerDto customerDto);

    void updateCustomer(UUID customerId, CustomerDto customerDto);

    void deleteById(UUID customerId);

}
