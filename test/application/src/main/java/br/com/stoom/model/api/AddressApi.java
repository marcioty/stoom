package br.com.stoom.model.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(builderClassName = "AddressApiBuilder", toBuilder = true)
@JsonDeserialize(builder = AddressApi.AddressApiBuilder.class)
public class AddressApi {

    @NotNull private String streetName;
    @NotNull private Integer number;
    private String complement;
    @NotNull private String neighbourhood;
    @NotNull private String city;
    @NotNull @Size(min = 2, max = 2) private String state;
    @NotNull private String country;
    @NotNull private String zipcode;
    private String latitude;
    private String longitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressApiBuilder {

    }
}
