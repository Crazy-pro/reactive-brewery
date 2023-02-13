package alex.klimchuk.reactive.brewery.web.controllers;

import alex.klimchuk.reactive.brewery.bootstrap.BeerLoader;
import alex.klimchuk.reactive.brewery.configs.BeerRouterConfig;
import alex.klimchuk.reactive.brewery.web.model.BeerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebClientV2IT {

    public static final String BASE_URL = "http://localhost:8080";

    WebClient webClient;

    @BeforeEach
    public void setUp() {
        webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
                .build();
    }

    @Test
    public void testDeleteBeer() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int beerId = 3;

        webClient.delete()
                .uri("/api/v2/beer/" + beerId)
                .retrieve()
                .toBodilessEntity()
                .flatMap(responseEntity -> {
                    countDownLatch.countDown();

                    return webClient.get()
                            .uri("/api/v2/beer/" + beerId)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(BeerDto.class);
                })
                .subscribe(savedDto -> {

                }, throwable -> {
                    countDownLatch.countDown();
                });
    }

    @Test
    public void testDeleteBeerNotFound() {
        int beerId = 4;

        webClient.delete()
                .uri("/api/v2/beer/" + beerId)
                .retrieve()
                .toBodilessEntity()
                .block();

        assertThrows(WebClientResponseException.NotFound.class, () -> {
            webClient.delete()
                    .uri("/api/v2/beer/" + beerId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        });
    }

    @Test
    public void testUpdateBeerNotFound() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final String newBeerName = "JTs Beer";
        final int beerId = 999;

        webClient.put()
                .uri(BeerRouterConfig.BEER_V2_URL + "/" + beerId)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(BeerDto.builder()
                        .beerName(newBeerName)
                        .upc("1233455")
                        .beerStyle("PALE_ALE")
                        .price(new BigDecimal("8.99"))
                        .build()))
                .retrieve()
                .toBodilessEntity()
                .subscribe(responseEntity -> {
                    assertThat(responseEntity.getStatusCode().is2xxSuccessful());
                }, throwable -> {
                    countDownLatch.countDown();
                });

        countDownLatch.await(1000L, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isZero();
    }

    @Test
    public void testUpdateBeer() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        final String newBeerName = "JTs Beer";
        final int beerId = 1;

        webClient.put()
                .uri(BeerRouterConfig.BEER_V2_URL + "/" + beerId)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(BeerDto.builder()
                        .beerName(newBeerName)
                        .upc("1233455")
                        .beerStyle("PALE_ALE")
                        .price(new BigDecimal("8.99"))
                        .build()))
                .retrieve()
                .toBodilessEntity()
                .subscribe(responseEntity -> {
                    assertThat(responseEntity.getStatusCode().is2xxSuccessful());

                    countDownLatch.countDown();
                });

        countDownLatch.await(500, TimeUnit.MILLISECONDS);

        webClient.get()
                .uri(BeerRouterConfig.BEER_V2_URL + "/" + beerId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerDto.class)
                .subscribe(beer -> {
                    assertThat(beer).isNotNull();
                    assertThat(beer.getBeerName()).isNotNull();
                    assertThat(beer.getBeerName()).isEqualTo(newBeerName);

                    countDownLatch.countDown();
                });

        countDownLatch.await(1000L, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isEqualTo(2L);
    }

    @Test
    public void testSaveBeer() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        BeerDto beerDto = BeerDto.builder()
                .beerName("JTs Beer")
                .upc("1233455")
                .beerStyle("PALE_ALE")
                .price(new BigDecimal("8.99"))
                .build();

        Mono<ResponseEntity<Void>> beerResponseMono = webClient.post()
                .uri(BeerRouterConfig.BEER_V2_URL)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();

        beerResponseMono.publishOn(Schedulers.parallel())
                .subscribe(responseEntity -> {
                    assertThat(responseEntity.getStatusCode().is2xxSuccessful());

                    countDownLatch.countDown();
                });

        countDownLatch.await(1000L, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isZero();
    }

    @Test
    public void testSaveBeerBadRequest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        BeerDto beerDto = BeerDto.builder()
                .price(new BigDecimal("8.99"))
                .build();

        Mono<ResponseEntity<Void>> beerResponseMono = webClient.post()
                .uri(BeerRouterConfig.BEER_V2_URL)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();

        beerResponseMono.subscribe(responseEntity -> {

        }, throwable -> {
            if (throwable.getClass().getName().equals("org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest")) {
                WebClientResponseException ex = (WebClientResponseException) throwable;

                if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isZero();
    }

    @Test
    public void testGetBeerByUPC() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Mono<BeerDto> beerDtoMono = webClient.get()
                .uri(BeerRouterConfig.BEER_V2_URL_WITH_UPC + "/" + BeerLoader.BEER_2_UPC)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {
            assertThat(beer).isNotNull();
            assertThat(beer.getBeerName()).isNotNull();

            countDownLatch.countDown();
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isZero();
    }

    @Test
    public void testGetBeerByUPCNotFound() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Mono<BeerDto> beerDtoMono = webClient.get()
                .uri(BeerRouterConfig.BEER_V2_URL_WITH_UPC + "/4484848393939292")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {

        }, throwable -> {
            countDownLatch.countDown();
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isZero();
    }

    @Test
    public void testGetBeerById() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Mono<BeerDto> beerDtoMono = webClient.get()
                .uri(BeerRouterConfig.BEER_V2_URL + "/" + 1)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {
            assertThat(beer).isNotNull();
            assertThat(beer.getBeerName()).isNotNull();

            countDownLatch.countDown();
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isZero();
    }

    @Test
    public void testGetBeerByIdNotFound() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Mono<BeerDto> beerDtoMono = webClient.get()
                .uri(BeerRouterConfig.BEER_V2_URL + "/" + 1333)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {

        }, throwable -> {
            countDownLatch.countDown();
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertThat(countDownLatch.getCount()).isZero();
    }

}
