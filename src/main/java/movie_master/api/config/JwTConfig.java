package movie_master.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwTConfig {
    @Value("${jwt.secret}")
    private String secret;

    public String getSecret() {
        return secret;
    }
}