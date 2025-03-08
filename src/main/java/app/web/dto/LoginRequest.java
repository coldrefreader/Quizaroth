package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters")
    private String username;


    @NotBlank(message = "Password cannot be blank")
    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters")
    private String password;
}
