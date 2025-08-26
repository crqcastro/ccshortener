package br.com.cesarcastro.apis.ccshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
public class CcshortenerApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));

        Map<String, Object> props = new HashMap<>();
        props.put("system.timezone", ZoneId.systemDefault().toString());

        SpringApplication app = new SpringApplication(CcshortenerApplication.class);
        app.setDefaultProperties(props);
        app.run(args);
    }
}