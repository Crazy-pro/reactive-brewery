package alex.klimchuk.reactive.brewery.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerDto {

    @Null
    private Integer id;

    @NotBlank
    private String beerName;

    @NotBlank
    private String beerStyle;

    private String upc;

    private BigDecimal price;

    private Integer quantityOnHand;

    private LocalDateTime createdDate;

    private LocalDateTime lastUpdatedDate;

}
