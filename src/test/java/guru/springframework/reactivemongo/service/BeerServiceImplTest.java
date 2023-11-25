package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mapper.BeerMapper;
import guru.springframework.reactivemongo.mapper.BeerMapperImpl;
import guru.springframework.reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerMapper beerMapper;

    BeerDTO beerDto;

    @BeforeEach
    void setUp() {
        beerDto = beerMapper.beerToBeerDto(getTestBeer());
    }

    @Test
    void findByBeerStyle() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        BeerDTO beerDTO = getSavedBeerDto();

        beerService.findByBeerStyle(beerDTO.getBeerStyle()).subscribe(dto -> {
            System.out.println(dto);
            atomicBoolean.set(true);
        });
        await().untilTrue(atomicBoolean);

    }

    @Test
    void saveBeer() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Mono<BeerDTO> savedMono = beerService.saveNewBeer(Mono.just(beerDto));

        savedMono.subscribe(beerDTO -> {
            System.out.println(beerDTO.getId());
            atomicBoolean.set(true);
        });
        await().untilTrue(atomicBoolean);
    }

    @Test
    void findFirstByBeerNameTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        BeerDTO beerDTO = getSavedBeerDto();
        Mono<BeerDTO> foundDto = beerService.findFirstByBeerName(beerDTO.getBeerName());

        foundDto.subscribe(dto -> {
            System.out.println(dto.getBeerName());
            atomicBoolean.set(true);
        });
        await().untilTrue(atomicBoolean);

    }

    private BeerDTO getSavedBeerDto() {
        return beerService.saveNewBeer(Mono.just(getTestBeerDto())).block();
    }

    private BeerDTO getTestBeerDto() {
        return new BeerMapperImpl().beerToBeerDto(getTestBeer());
    }

    private Beer getTestBeer() {
        return Beer.builder()
                .beerName("Zywiec")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .build();
    }

}
