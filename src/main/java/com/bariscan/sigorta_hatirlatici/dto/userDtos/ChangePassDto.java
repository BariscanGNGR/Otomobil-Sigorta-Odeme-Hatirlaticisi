package com.bariscan.sigorta_hatirlatici.dto.userDtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChangePassDto {
    private Long id;
    private String password;
    private String newPass;
    private String newPassAgain;
}
