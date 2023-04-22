package by.hackaton.bookcrossing.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewPasswordRequest {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}
