package br.com.stoom.service;

import br.com.stoom.entity.Address;
import br.com.stoom.exception.AddressNotFoundException;
import br.com.stoom.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);
    private LatitudeLongitudeService latitudeLongitudeService;
    private AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository, LatitudeLongitudeService latitudeLongitudeService) {
        this.addressRepository = addressRepository;
        this.latitudeLongitudeService = latitudeLongitudeService;
    }

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Address findById(UUID id) {
        return addressRepository.findById(id).orElseThrow(AddressNotFoundException::new);
    }

    public Address save(Address address) {
        log.info("Creating {}", address);
        return addressRepository.save(handleLatitudeAndLongitude(address));
    }

    public Address update(Address address) {
        log.debug("Updating {}", address);
        addressRepository.findById(address.getId()).orElseThrow(AddressNotFoundException::new);
        return addressRepository.save(handleLatitudeAndLongitude(address));
    }

    public void delete(UUID id) {
        addressRepository.deleteById(id);
    }

    private Address handleLatitudeAndLongitude(Address address) {
        log.info("Checking Latitude and Longitude.");
        if (isEmpty(address.getLatitude()) || isEmpty(address.getLongitude())) {
            return latitudeLongitudeService.findLatitudeAndLongitude(address);
        }
        return address;
    }
}
