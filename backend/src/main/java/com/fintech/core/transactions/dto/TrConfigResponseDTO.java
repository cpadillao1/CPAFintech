package com.fintech.core.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrConfigResponseDTO {
    private Long configId;
    private String mnemonic;
    private String reasonName;
}
