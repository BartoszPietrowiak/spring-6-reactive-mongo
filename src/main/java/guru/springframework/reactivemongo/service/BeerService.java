package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {
    Mono<BeerDTO> findFirstByBeerName(String beerName);
    Flux<BeerDTO> findByBeerStyle(String beerStyle);
    Flux<BeerDTO> listBeers();

    Mono<BeerDTO> getBeerById(String beerId);

    Mono<BeerDTO> saveNewBeer(Mono<BeerDTO> beerDTO);

    Mono<BeerDTO> saveNewBeer(BeerDTO beerDTO);

    Mono<BeerDTO> updateBeer(String beerId, BeerDTO beerDTO);

    Mono<BeerDTO> pathBeer(String beerId, BeerDTO beerDTO);

    Mono<Void> deleteBeer(String beerId);
}
