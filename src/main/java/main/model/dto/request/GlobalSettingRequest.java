package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.service.GlobalSettingService;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSettingRequest {

    @JsonProperty(GlobalSettingService.MULTIUSER_MODE_CODE)
    private boolean multiuserMode;

    @JsonProperty(GlobalSettingService.POST_PREMODERATION_CODE)
    private boolean postPreModeration;

    @JsonProperty(GlobalSettingService.STATISTICS_IS_PUBLIC_CODE)
    private boolean statisticsIsPublic;

}
