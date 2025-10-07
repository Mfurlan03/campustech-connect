package com.example.campustech.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL) // Ensures empty fields are ignored
public class WishlistResponseDTO {

    @JsonProperty("buyerId")
    private Long buyerId;

    @JsonProperty("laptopId")
    private Long laptopId;

    @JsonProperty("laptopBrand")
    private String laptopBrand;

    @JsonProperty("laptopModel")
    private String laptopModel;

    @JsonProperty("targetPrice")
    private Double targetPrice;
}
