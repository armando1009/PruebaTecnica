package com.prueba.backend.controller;

import com.prueba.backend.model.Transaccion;
import com.prueba.backend.repository.TransaccionRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final String BASE_URL = "https://trxdvpy.akisi.com:8443";

    private final TransaccionRepository repository;

    public AuthController(TransaccionRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) throws Exception {

        trustAllCertificates();

        String url = BASE_URL + "/cashout/authenticate";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        body.put("username", request.get("username"));
        body.put("password", request.get("password"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        return ResponseEntity.ok(response.getBody());
    }

    private void trustAllCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    @PostMapping("/consulta")
    public ResponseEntity<?> consulta(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request
    ) {
        try {
            trustAllCertificates();

            String url = BASE_URL + "/cashout/api/consulta";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/pago")
    public ResponseEntity<?> pago(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request
    ) {
        try {
            trustAllCertificates();

            String url = BASE_URL + "/cashout/api/pago";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            Map<String, Object> resp = response.getBody();

            Transaccion t = new Transaccion();
            t.setIdentificador((String) request.get("strIdentificador01"));
            t.setMonto(((Number) resp.get("dblCashAmount")).doubleValue());
            t.setComision(((Number) resp.get("dblCommissionFee")).doubleValue());
            t.setNombre((String) resp.get("strName"));
            t.setCodigoRespuesta((String) resp.get("strResponseCode"));
            t.setMensajeRespuesta((String) resp.get("strResponseMessage"));

            repository.save(t);

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/reportes")
    public ResponseEntity<?> obtenerReportes() {
        return ResponseEntity.ok(repository.findAll());
    }
}