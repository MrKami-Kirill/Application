package main.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "blog")
@Schema(description = "")
public class InitConfig {
    @Schema(description = "")
    private String title;
    @Schema(description = "")
    private String subtitle;
    @Schema(description = "")
    private String phone;
    @Schema(description = "")
    private String email;
    @Schema(description = "")
    private String copyright;
    @Schema(description = "")
    private String copyrightFrom;
}