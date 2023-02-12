package alex.klimchuk.reactive.brewery.web.mappers;

import alex.klimchuk.reactive.brewery.domain.Customer;
import alex.klimchuk.reactive.brewery.web.model.CustomerDto;
import org.mapstruct.Mapper;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto dto);

    CustomerDto customerToCustomerDto(Customer customer);

}
