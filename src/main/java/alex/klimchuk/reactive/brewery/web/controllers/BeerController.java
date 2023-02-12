package alex.klimchuk.reactive.brewery.web.controllers;

import alex.klimchuk.reactive.brewery.bootstrap.services.BeerService;
import alex.klimchuk.reactive.brewery.web.exceptions.NotFoundException;
import alex.klimchuk.reactive.brewery.web.model.BeerDto;
import alex.klimchuk.reactive.brewery.web.model.BeerPagedList;
import alex.klimchuk.reactive.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BeerController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerService beerService;

    @GetMapping(produces = {"/application/json"}, path = "beer")
    public ResponseEntity<Mono<BeerPagedList>> listBeers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                         @RequestParam(value = "beerName", required = false) String beerName,
                                                         @RequestParam(value = "beerStyle", required = false) BeerStyleEnum beerStyle,
                                                         @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand) {
        if (Objects.isNull(pageNumber) || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (Objects.isNull(pageSize) || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (Objects.isNull(showInventoryOnHand)) {
            showInventoryOnHand = false;
        }

        return ResponseEntity.ok(beerService.listBeers(beerName, beerStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand));
    }


    @GetMapping("/beer/{beerId}")
    public ResponseEntity<Mono<BeerDto>> getBeerById(@PathVariable("beerId") Integer beerId,
                                                     @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand) {
        if (Objects.isNull(showInventoryOnHand)) {
            showInventoryOnHand = false;
        }

        return ResponseEntity.ok(beerService.getById(beerId, showInventoryOnHand)
                .defaultIfEmpty(BeerDto.builder().build())
                .doOnNext(beerDto -> {
                    if (Objects.isNull(beerDto.getId())) {
                        throw new NotFoundException();
                    }
                }));
    }

    @GetMapping("/beerUpc/{upc}")
    public ResponseEntity<Mono<BeerDto>> getBeerByUpc(@PathVariable("upc") String upc) {
        return ResponseEntity.ok(beerService.getByUpc(upc));
    }

    @PostMapping(path = "/beer")
    public ResponseEntity<Void> saveNewBeer(@RequestBody @Validated BeerDto beerDto) {
        AtomicInteger atomicIntegerBeerId = new AtomicInteger();

        beerService.saveNewBeer(beerDto).subscribe(savedBeerDto -> atomicIntegerBeerId.set(savedBeerDto.getId()));

        return ResponseEntity.created(UriComponentsBuilder
                        .fromHttpUrl("https://api.springframework.guru/api/v1/beer/" + atomicIntegerBeerId.get())
                        .build().toUri())
                .build();
    }

    @PutMapping("/beer/{beerId}")
    public ResponseEntity<Void> updateBeerById(@PathVariable("beerId") Integer beerId, @RequestBody @Validated BeerDto beerDto) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerService.updateBeer(beerId, beerDto).subscribe(savedDto -> {
            if (Objects.nonNull(savedDto.getId())) {
                atomicBoolean.set(true);
            }
        });

        if (atomicBoolean.get()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/beer/{beerId}")
    public ResponseEntity<Void> deleteBeerById(@PathVariable("beerId") Integer beerId) {
        beerService.deleteBeerById(beerId);

        return ResponseEntity.ok().build();
    }

}
