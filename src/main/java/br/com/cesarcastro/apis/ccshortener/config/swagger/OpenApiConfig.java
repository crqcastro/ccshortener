package br.com.cesarcastro.apis.ccshortener.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
    private final String hostUrl;
    private final String appName;
    private final String description;
    private final String title;

    @Autowired
    public OpenApiConfig(Environment env,
                         @Value("${server.port}") Integer port,
                         @Value("${info.app.host.url}") String hostUrl,
                         @Value("${info.app.name}") String appName,
                         @Value("${info.app.description}") String description,
                         @Value("${info.app.title}") String title) {
        this.env = env;
        this.port = port;
        this.hostUrl = hostUrl;
        this.appName = appName;
        this.description = description;
        this.title = title;
    }

    @Bean
    public OpenAPI getConfigOpenApi(@Value("${info.app.version}") String appVersion) {
        final var envLocal = Arrays.asList(env.getActiveProfiles()).contains("local");
        final var apiVersion = "v" + appVersion.split("\\.")[0];

        List<Server> servers = new ArrayList<>();

        if (envLocal) {
            var url = "http://localhost:" + port + "/" + appName + "/" + apiVersion;
            log.info("Local server URL: {}", url);
            Server localServer = new Server()
                    .description("Local Server")
                    .url(url);

            servers.add(localServer);
        } else {
            var url = hostUrl + "/" + appName + "/" + apiVersion;
            Server developmentServer = new Server()
                    .description("Production Server")
                    .url(url);

            servers.add(developmentServer);
        }

        Info info = new Info()
                .title(title)
                .description(description)
                .version(appVersion);

        return new OpenAPI().info(info).servers(servers);
    }
}
