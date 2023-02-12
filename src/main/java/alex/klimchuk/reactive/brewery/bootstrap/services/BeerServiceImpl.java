package alex.klimchuk.reactive.brewery.bootstrap.services;

import alex.klimchuk.reactive.brewery.repositories.BeerRepository;
import alex.klimchuk.reactive.brewery.web.exceptions.NotFoundException;
import alex.klimchuk.reactive.brewery.web.model.BeerPagedList;
import alex.klimchuk.reactive.brewery.web.model.BeerStyleEnum;
import alex.klimchuk.reactive.brewery.domain.Beer;
import alex.klimchuk.reactive.brewery.web.mappers.BeerMapper;
import alex.klimchuk.reactive.brewery.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.empty;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * Copyright Alex Klimchuk (c) 2023.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final R2dbcEntityTemplate template;

    @Override
    @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false")
    public Mono<BeerPagedList> listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {
        boolean isHasName = StringUtils.hasText(beerName);
        boolean isHasStyle = !ObjectUtils.isEmpty(beerStyle);

        Query query = empty();

        if (isHasName && isHasStyle) {
            query = query(where("beerName").is(beerName).and("beerStyle").is(beerStyle));
        } else if (isHasName) {
            query = query(where("beerName").is(beerName));
        } else if (isHasStyle) {
            query = query(where("beerStyle").is(beerStyle));
        }

        return template.select(Beer.class)
                .matching(query.with(pageRequest))
                .all()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList())
                .map(beers -> new BeerPagedList(
                        beers,
                        PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize()),
                        beers.size())
                );
    }

    @Override
    @Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false")
    public Mono<BeerDto> getById(Integer beerId, Boolean showInventoryOnHand) {
        if (showInventoryOnHand) {
            return beerRepository.findById(beerId)
                    .map(beerMapper::beerToBeerDtoWithInventory);
        } else {
            return beerRepository.findById(beerId)
                    .map(beerMapper::beerToBeerDto);
        }
    }

    @Override
    @Cacheable(cacheNames = "beerUpcCache")
    public Mono<BeerDto> getByUpc(String upc) {
        return beerRepository.findByUpc(upc)
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDto> saveNewBeer(BeerDto beerDto) {
        return beerRepository.save(beerMapper.beerDtoToBeer(beerDto))
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDto> saveNewBeerMono(Mono<BeerDto> beerDto) {
        return beerDto.map(beerMapper::beerDtoToBeer)
                .flatMap(beerRepository::save)
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDto> updateBeer(Integer beerId, BeerDto beerDto) {
        return beerRepository.findById(beerId)
                .defaultIfEmpty(Beer.builder().build())
                .map(beer -> Beer.builder()
                        .beerName(beerDto.getBeerName())
                        .beerStyle(BeerStyleEnum.valueOf(beerDto.getBeerStyle()))
                        .price(beerDto.getPrice())
                        .upc(beerDto.getUpc())
                        .build())
                .flatMap(updatedBeer -> {
                    if (Objects.nonNull(updatedBeer.getId())) {
                        return beerRepository.save(updatedBeer);
                    } else {
                        return Mono.just(updatedBeer);
                    }
                })
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<Void> reactiveDeleteById(Integer beerId) {
        return beerRepository.findById(beerId)
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .map(Beer::getId)
                .flatMap(beerRepository::deleteById);
    }

    @Override
    public void deleteBeerById(Integer beerId) {
        beerRepository.deleteById(beerId).subscribe();
    }

}
