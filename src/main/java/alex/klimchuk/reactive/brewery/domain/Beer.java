package alex.klimchuk.reactive.brewery.domain;

import alex.klimchuk.reactive.brewery.web.model.BeerStyleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beer {

    @Id
    private Integer id;

    private Long version;

    private String beerName;

    private BeerStyleEnum beerStyle;

    private String upc;

    private Integer quantityOnHand;

    private BigDecimal price;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

}
