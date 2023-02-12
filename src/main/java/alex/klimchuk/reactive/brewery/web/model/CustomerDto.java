package alex.klimchuk.reactive.brewery.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

}
