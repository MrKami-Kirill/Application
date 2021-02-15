package main.api.response;

import lombok.Data;

@Data
public class BooleanResponse implements Response {

    private boolean result;

    public BooleanResponse() {
        this.result = false;
    }

    public BooleanResponse(boolean result) {
        this.result = result;
    }
}
