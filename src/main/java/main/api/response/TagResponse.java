package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse implements Response {

    private List<TagWithWeight> tags;

    public TagResponse(Map<String, Double> tagsMap) {
        this.tags = new LinkedList<>();
        for (String key : tagsMap.keySet()) {
            TagWithWeight tagWithWeight = new TagWithWeight(key, tagsMap.get(key));
            tags.add(tagWithWeight);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TagWithWeight {

        private String name;
        private double weight;
    }
}
