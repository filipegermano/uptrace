package br.com.uppersystems.uptrace.resource;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Teste {

    @GetMapping("/teste")
    public ResponseEntity<?> teste() {


        return ResponseEntity.ok("Teste API");

    }

    @GetMapping( "/teste/{id}")
    public ResponseEntity<?> teste(@PathVariable String id) throws Exception {


        log.info("Teste de log Info");
        log.warn("Teste de log warn");

        log.error("Teste de log error");


        //throw new Exception();

        return ResponseEntity.ok("Teste API {id}");

    }
    
    @PostMapping("/teste")
    public ResponseEntity<?> testePost(Pessoa pessoa) {


        return ResponseEntity.ok("Teste Post API");

    }
    
}
