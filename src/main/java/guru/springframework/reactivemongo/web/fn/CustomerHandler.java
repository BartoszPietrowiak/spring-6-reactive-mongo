package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {

    private final CustomerService customerService;
    private final Validator validator;

    private void validate(CustomerDTO customerDTO) {
        Errors errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
        validator.validate(customerDTO, errors);
        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }


    public Mono<ServerResponse> listCustomers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(customerService.listCustomers(), CustomerDTO.class);
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(customerService.getCustomerById(serverRequest.pathVariable("customerId"))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))), CustomerDTO.class);
    }

    public Mono<ServerResponse> saveNewCustomer(ServerRequest serverRequest) {
        return customerService.saveNewCustomer(serverRequest.bodyToMono(CustomerDTO.class)
                        .doOnNext(this::validate))
                .flatMap(customerDTO -> ServerResponse.created(UriComponentsBuilder
                        .fromPath(CustomerRouterConfig.CUSTOMER_BY_ID_PATH)
                        .build(customerDTO.getId())).build());
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(customerDTO -> customerService
                .updateCustomer(serverRequest.pathVariable("customerId"), customerDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(dto -> ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> pathCustomer(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(customerDTO -> customerService
                .pathCustomer(serverRequest.pathVariable("customerId"), customerDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(dto -> ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest serverRequest) {
        return customerService.getCustomerById(serverRequest.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customerDto -> customerService.deleteCustomer(serverRequest.pathVariable("customerId")))
                .then(ServerResponse.noContent().build());
    }
}
