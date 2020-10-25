package br.com.stoom.service;

import br.com.stoom.configuration.GoogleGeocodeApiProperties;
import br.com.stoom.entity.Address;
import br.com.stoom.exception.GoogleApiInvalidAddressInformationException;
import br.com.stoom.model.google.GeocodeResponse;
import br.com.stoom.model.google.Geometry;
import br.com.stoom.model.google.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Service
public class LatitudeLongitudeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GoogleGeocodeApiProperties apiProperties;

    public LatitudeLongitudeService(RestTemplate restTemplate, GoogleGeocodeApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    public Address findLatitudeAndLongitude(Address address) {
        GeocodeResponse geocodeResponseEntity = consumeGoogleApi(address);
        return buildAddressLatitudeAndLongitude(Objects.requireNonNull(geocodeResponseEntity), address).build();
    }

    private GeocodeResponse consumeGoogleApi(Address address) {
        ResponseEntity<GeocodeResponse> geocodingResponseEntity =
                restTemplate.getForEntity(buildUri(address), GeocodeResponse.class);
        if (geocodingResponseEntity.getStatusCode().isError()) {
            throw new GoogleApiInvalidAddressInformationException();
        }
        return geocodingResponseEntity.getBody();
    }

    private String buildUri(Address address) {
        return UriComponentsBuilder.fromHttpUrl(apiProperties.getBaseUrl())
                .queryParam("address", getFullAddress(address))
                .queryParam("key", apiProperties.getApiKey())
                .toUriString();
    }

    private String getFullAddress(Address address) {
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(address.getStreetName());
        fullAddress.append(", ");
        fullAddress.append(address.getNumber());
        fullAddress.append(". ");
        fullAddress.append(address.getCity());
        fullAddress.append(" - ");
        fullAddress.append(address.getState());
        fullAddress.append(", ");
        fullAddress.append(address.getCountry());
        fullAddress.append(". ");
        fullAddress.append(address.getZipcode());
        return fullAddress.toString().replaceAll(" ", "+");
    }

    private Address.AddressBuilder buildAddressLatitudeAndLongitude(GeocodeResponse geocodeResponse, Address address) {
        return geocodeResponse.getResults()
                .stream()
                .findFirst()
                .map(Result::getGeometry)
                .map(Geometry::getLocation)
                .map(location ->
                        address.toBuilder()
                                .latitude(location.getLatitude())
                                .longitude(location.getLongitude())
                )
                .orElseThrow(() -> new IllegalArgumentException("Failed to get data from Google Geocoding API"));
    }
}
