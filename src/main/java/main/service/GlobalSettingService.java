package main.service;


import lombok.extern.slf4j.Slf4j;
import main.model.dto.request.GlobalSettingRequest;
import main.model.dto.response.GlobalSettingResponse;
import main.model.dto.response.Response;
import main.model.entity.GlobalSetting;
import main.model.entity.User;
import main.repositories.GlobalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GlobalSettingService {

    public static final String MULTIUSER_MODE_CODE = "MULTIUSER_MODE";
    public static final String POST_PREMODERATION_CODE = "POST_PREMODERATION";
    public static final String STATISTICS_IS_PUBLIC_CODE = "STATISTICS_IS_PUBLIC";
    public static final String MULTIUSER_MODE_NAME = "Многопользовательский режим";
    public static final String POST_PREMODERATION_NAME = "Премодерация постов";
    public static final String STATISTICS_IS_PUBLIC_NAME = "Показывать всем статистику блога";

    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    @Autowired
    private UserService userService;

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
        log.info("Направляем ответ на запрос GET /api/settings cо следующими параметрами: {" +
                "HttpStatus: " + response.getStatusCode() + ", " +
                response.getBody() + "}");
        return response;
    }

    public boolean getGlobalSettingValue(String code) {
        String value = globalSettingRepository.getGlobalSettingValue(code);
        return convertStringToBooleanGlobalSettingValue(value);
    }


    public ResponseEntity<Response> setGlobalSettnigs(GlobalSettingRequest globalSettingRequest, HttpSession session) throws Exception {
        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }

        if (user.getIsModerator() == 0) {
            log.warn("Изменение глобавльных настроек запрещено, т.к. пользователь с ID:" + user.getId() + " не является модератором");
            throw new Exception("Пользователь не является модератором");
        }

        boolean isMultiuserMode = globalSettingRequest.isMultiuserMode();
        boolean isPostPreModeration = globalSettingRequest.isPostPreModeration();
        boolean isStatisticsIsPublic = globalSettingRequest.isStatisticsIsPublic();

        List<GlobalSetting> globalSettings = globalSettingRepository.findAll();
        for (GlobalSetting setting : globalSettings) {
            switch (setting.getCode()) {
                case MULTIUSER_MODE_CODE:
                    setting.setValue(isMultiuserMode ? "YES" : "NO");
                    break;
                case POST_PREMODERATION_CODE:
                    setting.setValue(isPostPreModeration ? "YES" : "NO");
                    break;
                case STATISTICS_IS_PUBLIC_CODE:
                    setting.setValue(isStatisticsIsPublic ? "YES" : "NO");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + setting.getCode());
            }
            globalSettingRepository.save(setting);
            log.info("Значение глобальной настройки '" + setting.getCode()+ "' изменилось на '" + setting.getValue() + "'");
        }
        Map<String, Boolean> responseMap = new HashMap<>();
        responseMap.put(GlobalSettingService.MULTIUSER_MODE_CODE, isMultiuserMode);
        responseMap.put(GlobalSettingService.POST_PREMODERATION_CODE, isPostPreModeration);
        responseMap.put(GlobalSettingService.STATISTICS_IS_PUBLIC_CODE, isStatisticsIsPublic);

        ResponseEntity<Response> response = new ResponseEntity<>(new GlobalSettingResponse(responseMap), HttpStatus.OK);
        log.info("Направляем ответ на запрос PUT /api/settings cо следующими параметрами: {" +
                "HttpStatus: " + response.getStatusCode() + ", " +
                response.getBody() + "}");
        return response;
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
