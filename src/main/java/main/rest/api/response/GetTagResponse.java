package main.rest.api.response;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class GetTagResponse implements Response {

    private List<TagWithWeight> tags;

    public GetTagResponse(Map<String, Double> tagsMap) {
        this.tags = new LinkedList<>();
        for (String key : tagsMap.keySet()) {
            TagWithWeight tagWithWeight = new TagWithWeight(key, tagsMap.get(key));
            tags.add(tagWithWeight);
        }
    }

    @Data
    static class TagWithWeight {

        private String name;
        private double weight;

        public TagWithWeight(String name, double weight) {
            this.name = name;
            this.weight = weight;
        }
    }
}
