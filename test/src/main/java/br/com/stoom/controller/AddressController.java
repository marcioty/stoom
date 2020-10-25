package br.com.stoom.controller;

import br.com.stoom.entity.Address;
import br.com.stoom.model.api.AddressApi;
import br.com.stoom.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.stoom.entity.Address.fromModel;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressApi> create(@Valid @RequestBody AddressApi addressApi) {
        Address persisted = addressService.save(fromModel(addressApi));
        return ResponseEntity.created(URI.create("/api/address/" + persisted.getId().toString())).body(persisted.toModel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressApi> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(addressService.findById(UUID.fromString(id)).toModel());
    }

    @GetMapping
    public ResponseEntity<List<Address>> getAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressApi> update(@PathVariable("id") String id, @Valid @RequestBody AddressApi addressApi) {
        Address address = fromModel(addressApi).toBuilder().id(UUID.fromString(id)).build();
        return ResponseEntity.ok(addressService.update(address).toModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id) {
        addressService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
