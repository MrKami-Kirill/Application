package main.service;


import lombok.extern.slf4j.Slf4j;
import main.api.response.GetGlobalSettingResponse;
import main.api.response.Response;
import main.model.entity.GlobalSetting;
import main.model.repositories.GlobalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GlobalSettingService {
    
    private static final String MULTIUSER_MODE_CODE = "MULTIUSER_MODE";
    private static final String POST_PREMODERATION_CODE = "POST_PREMODERATION";
    private static final String STATISTICS_IS_PUBLIC_CODE = "STATISTICS_IS_PUBLIC";
    private static final String MULTIUSER_MODE_NAME = "Многопользовательский режим";
    private static final String POST_PREMODERATION_NAME = "Премодерация постов";
    private static final String STATISTICS_IS_PUBLIC_NAME = "Показывать всем статистику блога";

    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    public ResponseEntity<Response> getGlobalSettings() {
        GetGlobalSettingResponse globalSettingResponse = new GetGlobalSettingResponse();
        List<GlobalSetting> globalSettingList = globalSettingRepository.findAll();
        for (GlobalSetting setting : globalSettingList) {
            switch (setting.getCode()) {
                case MULTIUSER_MODE_CODE:
                    globalSettingResponse.setMultiuserMode(convertStringToBooleanGlobalSettingValue(setting));
                    break;
                case POST_PREMODERATION_CODE:
                    globalSettingResponse.setPostPremoderation(convertStringToBooleanGlobalSettingValue(setting));
                    break;
                case STATISTICS_IS_PUBLIC_CODE:
                    globalSettingResponse.setStatisticsIsPublic(convertStringToBooleanGlobalSettingValue(setting));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + setting.getCode());
            }
        }
        ResponseEntity<Response> response = new ResponseEntity<>(globalSettingResponse, HttpStatus.OK);
        log.info("Направляем ответ на запрос /api/settings cо следующими параметрами: {" +
                "HttpStatus: " + response.getStatusCode() + ", " +
                response.getBody() + "}");
        return response;
    }


    private boolean convertStringToBooleanGlobalSettingValue(GlobalSetting setting) {
        boolean boolValue = false;
        switch (setting.getValue()) {
            case "YES":
                boolValue = true;
                break;
            case "NO":
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + setting.getValue());
            }
            return boolValue;
        }
}
