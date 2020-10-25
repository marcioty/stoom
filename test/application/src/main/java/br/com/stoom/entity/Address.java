package br.com.stoom.entity;

import br.com.stoom.model.api.AddressApi;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NonNull
    private String streetName;
    @NonNull
    private Integer number;
    private String complement;
    @NonNull
    private String neighbourhood;
    @NonNull
    private String city;
    @NonNull
    private String state;
    @NonNull
    private String country;
    @NonNull
    private String zipcode;
    private String latitude;
    private String longitude;

    public static Address fromModel(AddressApi addressApi) {
        return Address.builder()
                .streetName(addressApi.getStreetName())
                .number(addressApi.getNumber())
                .complement(addressApi.getComplement())
                .neighbourhood(addressApi.getNeighbourhood())
                .city(addressApi.getCity())
                .state(addressApi.getState())
                .country(addressApi.getCountry())
                .zipcode(addressApi.getZipcode())
                .longitude(addressApi.getLongitude())
                .latitude(addressApi.getLatitude())
                .build();
    }

    public AddressApi toModel() {
        return AddressApi.builder()
                .streetName(this.streetName)
                .number(this.number)
                .complement(this.complement)
                .neighbourhood(this.neighbourhood)
                .city(this.city)
                .state(this.state)
                .country(this.country)
                .zipcode(this.zipcode)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .build();
    }
}

