package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mapper.BeerMapper;
import guru.springframework.reactivemongo.mapper.BeerMapperImpl;
import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.service.BeerService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    BeerService beerService;

    @Test
    @Order(2)
    void listBeers() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(4);
    }

    @Test
    @Order(1)
    void getBeerById() {
        BeerDTO beerDTO = getSavedBeerDto();

        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(BeerRouterConfig.BEER_BY_ID_PATH, beerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(1)
    void getBeerByIdNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(BeerRouterConfig.BEER_BY_ID_PATH, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void saveNewBeer() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(getTestBeerDto()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    @Order(3)
    void saveNewBeerBadData() {
        BeerDTO testBeer = getTestBeerDto();
        testBeer.setBeerName("");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void updateBeer() {
        BeerDTO beerDTO = getSavedBeerDto();
        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(BeerRouterConfig.BEER_BY_ID_PATH, beerDTO.getId())
                .body(Mono.just(beerDTO), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(4)
    void updateBeerNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(BeerRouterConfig.BEER_BY_ID_PATH, 999)
                .body(Mono.just(getTestBeerDto()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void updateBeerBadData() {
        BeerDTO testBeer = getTestBeerDto();
        testBeer.setBeerStyle("");
        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(BeerRouterConfig.BEER_BY_ID_PATH, 1)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(999)
    void deleteBeer() {
        BeerDTO beerDTO = getSavedBeerDto();
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete().uri(BeerRouterConfig.BEER_BY_ID_PATH, beerDTO.getId())
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(999)
    void deleteBeerNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete().uri(BeerRouterConfig.BEER_BY_ID_PATH, 999)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
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
