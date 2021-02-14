package main.rest.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
public class BadRequestMessageResponse implements Response {

    private String message;

    public BadRequestMessageResponse(String... args) {
        this.message = Arrays.stream(args).filter(s ->
                (s != null && !s.isBlank()) && !s.equalsIgnoreCase(""))
                .collect(Collectors.joining(". "));
    }
}
