package br.com.uppersystems.uptrace.resource;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Teste {

    @GetMapping("/teste")
    public ResponseEntity<?> teste() {


        return ResponseEntity.ok("Teste API");

    }

    @GetMapping( "/teste/{id}")
    public ResponseEntity<?> teste(@PathVariable String id) {


        log.info("Teste de log Info");
        log.warn("Teste de log warn");

        log.warn("Teste de log error");

        return ResponseEntity.ok("Teste API {id}");

    }
}
