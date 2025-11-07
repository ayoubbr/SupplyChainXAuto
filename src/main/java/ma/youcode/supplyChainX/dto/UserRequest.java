package ma.youcode.supplyChainX.dto;

import lombok.*;
import ma.youcode.supplyChainX.shared.enums.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
}