package alex.klimchuk.reactive.brewery.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private UUID id;

    private String name;

}
