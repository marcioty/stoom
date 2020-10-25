package br.com.stoom.model.google;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "GeometryBuilder", toBuilder = true)
@JsonDeserialize(builder = Geometry.GeometryBuilder.class)
public class Geometry {

    private Location location;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GeometryBuilder {

    }
}
