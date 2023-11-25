package guru.springframework.reactivemongo.web.fn;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class CustomerRouterConfig {
    public static final String CUSTOMER_PATH = "/api/v3/customer";
    public static final String CUSTOMER_BY_ID_PATH = "/api/v3/customer/{customerId}";

    private final CustomerHandler handler;

    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return route()
                .GET(CUSTOMER_PATH, accept(MediaType.APPLICATION_JSON), handler::listCustomers)
                .GET(CUSTOMER_BY_ID_PATH, accept(MediaType.APPLICATION_JSON), handler::getCustomerById)
                .POST(CUSTOMER_PATH, accept(MediaType.APPLICATION_JSON), handler::saveNewCustomer)
                .PUT(CUSTOMER_BY_ID_PATH, accept(MediaType.APPLICATION_JSON), handler::updateCustomer)
                .PATCH(CUSTOMER_BY_ID_PATH, accept(MediaType.APPLICATION_JSON), handler::pathCustomer)
                .DELETE(CUSTOMER_BY_ID_PATH, accept(MediaType.APPLICATION_JSON), handler::deleteCustomer)
                .build();
    }
}
