package main.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadRequestMessageResponse implements Response {
    private boolean result;
    private HashMap<String, String> errors;

    public BadRequestMessageResponse(HashMap<String, String> errors) {
        this.result = false;
        this.errors = errors;
    }


}
