package alex.klimchuk.reactive.brewery.bootstrap.services;

import alex.klimchuk.reactive.brewery.web.model.BeerPagedList;
import alex.klimchuk.reactive.brewery.web.model.BeerStyleEnum;
import alex.klimchuk.reactive.brewery.web.model.BeerDto;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
public interface BeerService {

    Mono<BeerPagedList> listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    Mono<BeerDto> getById(Integer beerId, Boolean showInventoryOnHand);

    Mono<BeerDto> getByUpc(String upc);

    Mono<BeerDto> saveNewBeer(BeerDto beerDto);

    Mono<BeerDto> saveNewBeerMono(Mono<BeerDto> beerDto);

    Mono<BeerDto> updateBeer(Integer beerId, BeerDto beerDto);

    Mono<Void> reactiveDeleteById(Integer beerId);

    void deleteBeerById(Integer beerId);

}
