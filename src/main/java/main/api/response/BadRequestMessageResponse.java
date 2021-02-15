package main.api.response;

import lombok.Data;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
public class BadRequestMessageResponse implements Response {

    private boolean result;
    private String message;

    public BadRequestMessageResponse(String... args) {
        this.result = false;
        this.message = Arrays.stream(args).filter(s ->
                (s != null && !s.isBlank()) && !s.equalsIgnoreCase(""))
                .collect(Collectors.joining(". "));
    }
}
