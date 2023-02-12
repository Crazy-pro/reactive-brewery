package alex.klimchuk.reactive.brewery.web.controllers;

import alex.klimchuk.reactive.brewery.bootstrap.BeerLoader;
import alex.klimchuk.reactive.brewery.bootstrap.services.BeerService;
import alex.klimchuk.reactive.brewery.web.model.BeerDto;
import alex.klimchuk.reactive.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@WebFluxTest(BeerController.class)
public class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    BeerService beerService;

    BeerDto validBeer;

    @BeforeEach
    public void setUp() {
        validBeer = BeerDto.builder()
                .beerName("Test beer")
                .beerStyle("PALE_ALE")
                .upc(BeerLoader.BEER_1_UPC)
                .build();
    }

    @Test
    public void testListBeers() {
        List<BeerDto> beerList = Collections.singletonList(validBeer);
        BeerPagedList beerPagedList = new BeerPagedList(beerList, PageRequest.of(1,1), beerList.size());

        given(beerService.listBeers(any(), any(), any(), any())).willReturn(Mono.just(beerPagedList));

        webTestClient.get()
                .uri("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerPagedList.class);
    }

    @Test
    public void testGetBeerById() {
        int beerId = 1;

        given(beerService.getById(any(), any())).willReturn(Mono.just(validBeer));

        webTestClient.get()
                .uri("/api/v1/beer/" + beerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));
    }

    @Test
    public void testGetBeerByUPC() {
        given(beerService.getByUpc(any())).willReturn(Mono.just(validBeer));

        webTestClient.get()
                .uri("/api/v1/beerUpc/" + validBeer.getUpc())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));
    }

}
