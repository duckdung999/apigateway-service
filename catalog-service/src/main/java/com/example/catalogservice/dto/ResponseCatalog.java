package com.example.catalogservice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // json 반환값에 null은 직렬화해서 넣기 싫기 때문에, null은 빼준다.
public class ResponseCatalog {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

}
