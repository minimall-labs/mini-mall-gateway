package com.minimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
public class SwaggerUiConfig {

    /**
     * Spring Cloud Gateway + springdoc webflux sometimes skips the welcome redirect.
     * Serve a tiny aggregator page that loads Swagger UI from webjars.
     */
    @Bean
    public RouterFunction<ServerResponse> swaggerUiRouter() {
        String html = """
                <!DOCTYPE html>
                <html lang="zh-CN">
                <head>
                  <meta charset="UTF-8"/>
                  <title>mini-mall API</title>
                  <link rel="stylesheet" href="/webjars/swagger-ui/swagger-ui.css"/>
                </head>
                <body>
                <div id="swagger-ui"></div>
                <script src="/webjars/swagger-ui/swagger-ui-bundle.js"></script>
                <script src="/webjars/swagger-ui/swagger-ui-standalone-preset.js"></script>
                <script>
                  window.ui = SwaggerUIBundle({
                    urls: [
                      { name: 'user-service', url: '/docs/user/v3/api-docs' },
                      { name: 'product-service', url: '/docs/product/v3/api-docs' },
                      { name: 'inventory-service', url: '/docs/inventory/v3/api-docs' },
                      { name: 'order-service', url: '/docs/order/v3/api-docs' },
                      { name: 'payment-service', url: '/docs/payment/v3/api-docs' }
                    ],
                    'urls.primaryName': 'order-service',
                    dom_id: '#swagger-ui',
                    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
                    layout: 'StandaloneLayout',
                    tryItOutEnabled: true,
                    persistAuthorization: true
                  });
                </script>
                </body>
                </html>
                """;
        return RouterFunctions.route()
                .GET("/swagger-ui.html", req -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .bodyValue(html))
                .GET("/swagger-ui/index.html", req -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .bodyValue(html))
                .GET("/", req -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build())
                .build();
    }
}
