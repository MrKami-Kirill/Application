package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.service.GlobalSettingService;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на сохранение глобальных настроек блога")
public class GlobalSettingRequest {

    @Schema(description = GlobalSettingService.MULTIUSER_MODE_NAME)
    @JsonProperty(GlobalSettingService.MULTIUSER_MODE_CODE)
    private boolean multiuserMode;

    @Schema(description = GlobalSettingService.POST_PREMODERATION_NAME)
    @JsonProperty(GlobalSettingService.POST_PREMODERATION_CODE)
    private boolean postPreModeration;

    @Schema(description = GlobalSettingService.STATISTICS_IS_PUBLIC_NAME)
    @JsonProperty(GlobalSettingService.STATISTICS_IS_PUBLIC_CODE)
    private boolean statisticsIsPublic;

}
