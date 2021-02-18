package main.service;


import lombok.extern.slf4j.Slf4j;
import main.api.response.GlobalSettingResponse;
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

    public ResponseEntity<Response> getGlobalSettingsResponse() {
        GlobalSettingResponse globalSettingResponse = new GlobalSettingResponse();
        List<GlobalSetting> globalSettings = globalSettingRepository.findAll();
        for (GlobalSetting setting : globalSettings) {
            switch (setting.getCode()) {
                case MULTIUSER_MODE_CODE:
                    globalSettingResponse.setMultiuserMode(convertStringToBooleanGlobalSettingValue(setting.getValue()));
                    break;
                case POST_PREMODERATION_CODE:
                    globalSettingResponse.setPostPremoderation(convertStringToBooleanGlobalSettingValue(setting.getValue()));
                    break;
                case STATISTICS_IS_PUBLIC_CODE:
                    globalSettingResponse.setStatisticsIsPublic(convertStringToBooleanGlobalSettingValue(setting.getValue()));
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

    public boolean getGlobalSettingValue(String code) {
        String value = globalSettingRepository.getGlobalSettingValue(code);
        return convertStringToBooleanGlobalSettingValue(value);
    }


    private boolean convertStringToBooleanGlobalSettingValue(String value) {
        boolean boolValue = false;
        switch (value) {
            case "YES":
                boolValue = true;
                break;
            case "NO":
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + value);
            }
            return boolValue;
        }
}
