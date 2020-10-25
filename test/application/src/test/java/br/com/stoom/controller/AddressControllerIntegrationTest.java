package br.com.stoom.controller;

import br.com.stoom.entity.Address;
import br.com.stoom.model.api.AddressApi;
import br.com.stoom.repository.AddressRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Testing for Address API")
class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("[Create] Create address with all data")
    public void test_create_should_return_201() throws Exception {
        AddressApi addressApi = realAddressApi();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressApi)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        Address address = repository.findAll().stream().findFirst().orElse(null);
        assertThat(address).extracting(Address::getStreetName).isEqualTo(addressApi.getStreetName());
    }

    @Test
    @DisplayName("[Create] Create address without Latitude and Longitude")
    public void test_create_without_lat_lon_should_call_google_geocode_and_return_201() throws Exception {
        AddressApi addressApi = realAddressApi();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(realAddressApiWithoutLatitudeLongitude())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        Address address = repository.findAll().stream().findFirst().orElse(null);
        assertThat(address).extracting(Address::getLatitude, Address::getLongitude)
                .contains(addressApi.getLatitude(), addressApi.getLongitude());
    }

    @Test
    @DisplayName("[Create] Create address with invalid address data and without latitude and longitude")
    public void test_create_with_invalid_data_and_without_lat_lon_should_return_400() throws Exception {
        AddressApi addressApi = anAddress().toModel();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressApi.toBuilder()
                        .streetName("invalidStreetName")
                        .latitude(null)
                        .longitude(null)
                        .build()))).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[Create] Create address with empty mandatory field")
    public void test_create_with_empty_mandatory_field_should_return_400() throws Exception {
        AddressApi addressApi = realAddressApi().toBuilder().streetName(null).build();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressApi))).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[Read] Get all saved addresses")
    public void test_find_all_saved_addresses_should_return_list() throws Exception {
        Address address = createSimpleData(repository);
        String responseBody = mockMvc.perform(get("/api/address").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AddressApi> addressApi = objectMapper.readValue(responseBody, new TypeReference<List<AddressApi>>() {
        });

        assertThat(addressApi).hasSize(1)
                .extracting(AddressApi::getCity, AddressApi::getLatitude, AddressApi::getLongitude)
                .contains(tuple("City", "-22.123456", "-44.123456"));
    }

    @Test
    @DisplayName("[Read] Get all addresses when there is no data")
    public void test_find_all_with_no_saved_addresses_should_return_empty_list() throws Exception {
        String responseBody = mockMvc.perform(get("/api/address").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AddressApi> addressApi = objectMapper.readValue(responseBody, new TypeReference<List<AddressApi>>() {
        });

        assertThat(addressApi).hasSize(0);
    }

    @Test
    @DisplayName("[Read] Get an address by id")
    public void test_findById_should_return_requested_address() throws Exception {
        Address address = createSimpleData(repository);
        String responseBody =
                mockMvc.perform(get("/api/address/" + address.getId().toString()).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        AddressApi addressApi = objectMapper.readValue(responseBody, AddressApi.class);

        assertThat(addressApi)
                .extracting(AddressApi::getCity, AddressApi::getLatitude, AddressApi::getLongitude)
                .contains("City", "-22.123456", "-44.123456");
    }

    @Test
    @DisplayName("[Read] Get a nonexistent address id")
    public void test_findById_with_nonexistent_id_should_return_not_found_with_message() throws Exception {
        String responseBody =
                mockMvc.perform(get("/api/address/" + UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertThat(responseBody).isEqualTo("Address not found");
    }

    @Test
    @DisplayName("[Delete] Delete address for a nonexistent address id")
    public void test_delete_with_nonexistent_id_should_return_not_found_with_message() throws Exception {
        String responseBody = mockMvc.perform(delete("/api/address/" +
                UUID.randomUUID()
                        .toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(responseBody).isEqualTo("Address not found");
    }

    @Test
    @DisplayName("[Delete] Delete address by id")
    public void test_delete_by_id_should_return_204() throws Exception {
        Address address = createSimpleData(repository);

        mockMvc.perform(delete("/api/address/" + address.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("[Update] Update with valid address data")
    public void test_update_should_return_200() throws Exception {
        Address address = repository.save(anAddress());
        AddressApi addressApi = realAddressApi();
        mockMvc.perform(put("/api/address/" + address.getId().toString()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressApi)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[Update] Update address without Latitude and Longitude")
    public void test_update_without_lat_lon_should_call_google_geocode_and_return_200() throws Exception {
        Address savedAddress = repository.save(anAddress());

        AddressApi addressApi = realAddressApi();
        mockMvc.perform(put("/api/address/" + savedAddress.getId().toString()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(realAddressApiWithoutLatitudeLongitude())))
                .andExpect(status().isOk());

        Address updatedAddress = repository.findAll().stream().findFirst().orElse(null);

        assertThat(updatedAddress).extracting(Address::getLatitude, Address::getLongitude)
                .contains(addressApi.getLatitude(), addressApi.getLongitude());
    }

    @Test
    @DisplayName("[Update] Update address with invalid address data and without latitude and longitude")
    public void test_update_with_invalid_data_and_without_lat_lon_should_return_400() throws Exception {
        Address address = repository.save(anAddress());
        AddressApi addressApi = realAddressApi();
        mockMvc.perform(put("/api/address/" + address.getId().toString()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressApi.toBuilder().latitude(null).build())))
                .andExpect(status().isOk());

        String latitude = repository.findAll().stream().findFirst().map(Address::getLatitude).orElse(null);

        assertThat(latitude).isNotNull();
    }

    @Test
    @DisplayName("[Update] Update address with empty mandatory field")
    public void test_update_with_empty_mandatory_field_should_return_400() throws Exception {
        Address address = repository.save(anAddress());
        AddressApi addressApi = realAddressApi();
        mockMvc.perform(put("/api/address/" + address.getId().toString()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressApi.toBuilder().zipcode(null).build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[Update] Update address for a nonexistent id")
    public void test_update_with_nonexistent_id_should_return_not_found_with_message() throws Exception {
        AddressApi addressApi = realAddressApi();

        String responseBody = mockMvc.perform(put("/api/address/" + UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressApi)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(responseBody).isEqualTo("Address not found");
    }

    private Address createSimpleData(AddressRepository addressRepository) {
        return addressRepository.save(anAddress());
    }

    private AddressApi realAddressApi() {
        return aRealAddress().toModel();
    }

    private AddressApi realAddressApiWithoutLatitudeLongitude() {
        return aRealAddress().toModel().toBuilder().latitude(null).longitude(null).build();
    }

    private Address anAddress() {
        return Address.builder()
                .streetName("Name")
                .number(123)
                .complement("Complement")
                .neighbourhood("Neighbourhood")
                .city("City")
                .state("State")
                .country("BR")
                .zipcode("13000-000")
                .latitude("-22.123456")
                .longitude("-44.123456")
                .build();
    }

    private Address aRealAddress() {
        return Address.builder()
                .streetName("R. Zuneide Aparecida Marin")
                .city("Campinas")
                .country("BR")
                .latitude("-22.8354045")
                .longitude("-47.0787762")
                .neighbourhood("Jardim Santa Genebra II (Barao Geraldo)")
                .number(43)
                .state("SP")
                .zipcode("13084-780")
                .build();
    }
}