package org.innowise.internship.orderservice.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@NoArgsConstructor
@Setter
@Getter
public class UserResponseDTO {
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
