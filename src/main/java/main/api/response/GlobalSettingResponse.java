package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.service.GlobalSettingService;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSettingResponse implements Response  {

    @JsonProperty(GlobalSettingService.MULTIUSER_MODE_CODE)
    private boolean multiuserMode;

    @JsonProperty(GlobalSettingService.POST_PREMODERATION_CODE)
    private boolean postPremoderation;

    @JsonProperty(GlobalSettingService.STATISTICS_IS_PUBLIC_CODE)
    private boolean statisticsIsPublic;

    public GlobalSettingResponse(Map<String, Boolean> settings) {
        for (String key : settings.keySet()) {
            switch (key) {
                case (GlobalSettingService.MULTIUSER_MODE_NAME):
                    multiuserMode = settings.get(GlobalSettingService.MULTIUSER_MODE_NAME);
                    break;
                case (GlobalSettingService.POST_PREMODERATION_CODE):
                    postPremoderation = settings.get(GlobalSettingService.POST_PREMODERATION_CODE);
                    break;
                case (GlobalSettingService.STATISTICS_IS_PUBLIC_CODE):
                    statisticsIsPublic = settings.get(GlobalSettingService.STATISTICS_IS_PUBLIC_CODE);
                    break;
            }
        }
    }

}