package br.com.stoom.model.google;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "GeocodeResponseBuilder", toBuilder = true)
@JsonDeserialize(builder = GeocodeResponse.GeocodeResponseBuilder.class)
public class GeocodeResponse {

    private List<Result> results;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GeocodeResponseBuilder {

    }
}
