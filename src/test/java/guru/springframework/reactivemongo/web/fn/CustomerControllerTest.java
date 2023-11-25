package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.mapper.CustomerMapperImpl;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerService customerService;

    @Test
    void listCustomers() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(5);
    }

    @Test
    void getCustomerById() {
        CustomerDTO customerDTO = getSavedCustomerDto();
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_BY_ID_PATH, customerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    @Test
    void getCustomerByIdNotFound() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_BY_ID_PATH, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void saveNewCustomer() {
        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(getTestCustomerDto()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void saveNewCustomerBadData() {
        CustomerDTO testCustomer = getTestCustomerDto();
        testCustomer.setName("");
        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateCustomer() {
        CustomerDTO customerDTO = getSavedCustomerDto();
        webTestClient.put().uri(CustomerRouterConfig.CUSTOMER_BY_ID_PATH, customerDTO.getId())
                .body(Mono.just(getTestCustomerDto()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateCustomerBadData() {
        CustomerDTO testCustomer = getSavedCustomerDto();
        testCustomer.setName("");
        webTestClient.put().uri(CustomerRouterConfig.CUSTOMER_BY_ID_PATH, testCustomer.getId())
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateCustomerNotFound() {

        webTestClient.put().uri(CustomerRouterConfig.CUSTOMER_BY_ID_PATH, 999)
                .body(Mono.just(getTestCustomerDto()), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteCustomer() {
        CustomerDTO customerDTO = getSavedCustomerDto();

        webTestClient.delete().uri(CustomerRouterConfig.CUSTOMER_BY_ID_PATH, customerDTO.getId())
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteCustomerNotFound() {
        webTestClient.delete().uri(CustomerRouterConfig.CUSTOMER_BY_ID_PATH, 999)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }

    private CustomerDTO getSavedCustomerDto() {
        return customerService.saveNewCustomer(Mono.just(getTestCustomerDto())).block();
    }

    private CustomerDTO getTestCustomerDto() {
        return new CustomerMapperImpl().customerToCustomerDto(getTestCustomer());
    }

    private Customer getTestCustomer() {
        return Customer.builder()
                .name("John")
                .build();
    }
}
