package alex.klimchuk.reactive.brewery.web.mappers;

import alex.klimchuk.reactive.brewery.domain.Beer;
import alex.klimchuk.reactive.brewery.web.model.BeerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Mapper(uses = {DateMapper.class})
public interface BeerMapper {

    @Mapping(target = "quantityOnHand", ignore = true)
    BeerDto beerToBeerDto(Beer beer);

    BeerDto beerToBeerDtoWithInventory(Beer beer);

    Beer beerDtoToBeer(BeerDto dto);

}
