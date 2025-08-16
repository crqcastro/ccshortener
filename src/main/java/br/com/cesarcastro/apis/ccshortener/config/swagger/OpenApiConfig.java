package br.com.cesarcastro.apis.ccshortener.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
@Slf4j
public class OpenApiConfig {
    private final Environment env;
    private final Integer port;

    @Autowired
    public OpenApiConfig(Environment env, @Value("${server.port}") Integer port) {
        this.env = env;
        this.port = port;
    }

    @Bean
    public OpenAPI getConfigOpenApi(@Value("${info.app.version}") String appVersion) {
        final var appName = "ccshortener";
        final var envLocal = Arrays.asList(env.getActiveProfiles()).contains("local");
        final var envProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
        final var apiVersion = "v" + appVersion.split("\\.")[0];

        List<Server> servers = new ArrayList<>();

        if (envLocal) {
            var url = "http://localhost:" + port + "/" + appName + "/" + apiVersion;
            log.info("Local server URL: {}", url);
            Server localServer = new Server()
                    .description("Local Server")
                    .url(url);

            servers.add(localServer);
        } else if (envProd) {
            var url = "https://apis.cc.dev.br" + "/" + appName + "/" + apiVersion;
            log.info("Local server URL: {}", url);
            Server productionServer = new Server()
                    .description("Production server (uses live data)")
                    .url(url);

            servers.add(productionServer);
        } else {
            var url = "https://apis-dev.cc.dev.br" + "/" + appName + "/" + apiVersion;
            log.info("Local server URL: {}", url);
            Server developmentServer = new Server()
                    .description("Development Server (uses mock data)")
                    .url(url);

            servers.add(developmentServer);
        }

        Info info = new Info()
                .title("URL SHORTENER API")
                .description("URL shortening service API")
                .version(appVersion);

        return new OpenAPI().info(info).servers(servers);
    }
}
