package com.example.catalogservice.controller;


import com.example.catalogservice.dto.ResponseCatalog;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.entity.CatalogEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/catalog-service")
public class CatalogController {
    private final Environment env;
    private final CatalogService catalogService;

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's good Woriking %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs")
    //List<ResponseUser>, list를 타입으로 갖는 응답데이터를 담은 httpEntity의 하위 클래스.
    public ResponseEntity<List<ResponseCatalog>> getResponseCatalog(){
        Iterable<CatalogEntity> catalogList = catalogService.getAllCatalogs();

        List<ResponseCatalog> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        catalogList.forEach( v -> {
            result.add(mapper.map(v, ResponseCatalog.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
