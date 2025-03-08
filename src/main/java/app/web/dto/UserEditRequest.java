package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {

    @Size(max = 20, message = "First name should not be over 20 characters")
    private String firstName;

    @Size(max = 20, message = "Last name should not be over 20 characters")
    private String lastName;

    @Email(message = "Provide the correct email format")
    private String email;
}
