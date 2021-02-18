package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostByCalendarResponse implements Response {
    private List<Integer> years;
    private HashMap<String, Integer> posts;

    public PostByCalendarResponse(List<Integer> yearList, Map<Date, Integer> postsMap) {
        this.years = new LinkedList<>(yearList);
        years.sort(Comparator.comparingInt(o -> o));
        posts = new HashMap<>();
        for (Date d : postsMap.keySet()) {
            posts.put(String.valueOf(d), postsMap.get(d));
        }
    }
}
