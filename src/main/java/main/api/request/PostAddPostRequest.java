package main.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAddPostRequest implements Request {
    private String timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
