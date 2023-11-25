package guru.springframework.reactivemongo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerDTO {
    private String id;
    @NotBlank
    @NotNull
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
