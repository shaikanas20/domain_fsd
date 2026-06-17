package com.cinereserve.catalog.service;

import com.cinereserve.catalog.dto.LocationRequest;
import com.cinereserve.catalog.exception.CatalogException;
import com.cinereserve.catalog.model.Location;
import com.cinereserve.catalog.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @org.springframework.cache.annotation.CacheEvict(value = "locations", allEntries = true)
    public Location createLocation(LocationRequest request) {
        Location location = Location.builder()
                .name(request.name())
                .city(request.city())
                .state(request.state())
                .build();
        return locationRepository.save(location);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "locations", allEntries = true)
    public Location updateLocation(String id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Location not found with ID: " + id));
        location.setName(request.name());
        location.setCity(request.city());
        location.setState(request.state());
        return locationRepository.save(location);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "locations", allEntries = true)
    public void deleteLocation(String id) {
        if (!locationRepository.existsById(id)) {
            throw new CatalogException("Location not found with ID: " + id);
        }
        locationRepository.deleteById(id);
    }

    @org.springframework.cache.annotation.Cacheable(value = "locations", key = "'all'")
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @org.springframework.cache.annotation.Cacheable(value = "locations", key = "#id")
    public Location getLocationById(String id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Location not found with ID: " + id));
    }
}
