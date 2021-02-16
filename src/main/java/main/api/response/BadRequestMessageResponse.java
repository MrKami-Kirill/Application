package main.api.response;

import lombok.Data;

import java.util.HashMap;

@Data
public class BadRequestMessageResponse implements Response {
    private boolean result;
    private HashMap<String, String> errors;

    public BadRequestMessageResponse(HashMap<String, String> errors) {
        this.result = false;
        this.errors = errors;
    }


}
