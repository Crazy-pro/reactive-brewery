package alex.klimchuk.reactive.brewery.repositories;

import alex.klimchuk.reactive.brewery.domain.Beer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {

    Mono<Beer> findByUpc(String upc);

}
