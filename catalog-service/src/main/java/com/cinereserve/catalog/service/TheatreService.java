package com.cinereserve.catalog.service;

import com.cinereserve.catalog.dto.TheatreRequest;
import com.cinereserve.catalog.exception.CatalogException;
import com.cinereserve.catalog.model.Theatre;
import com.cinereserve.catalog.repository.TheatreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheatreService {

    private final TheatreRepository theatreRepository;

    public TheatreService(TheatreRepository theatreRepository) {
        this.theatreRepository = theatreRepository;
    }

    @org.springframework.cache.annotation.CacheEvict(value = "theatres", allEntries = true)
    public Theatre createTheatre(TheatreRequest request) {
        Theatre theatre = Theatre.builder()
                .name(request.name())
                .locationId(request.locationId())
                .address(request.address())
                .status("PENDING") // Defaults to pending approval
                .build();
        return theatreRepository.save(theatre);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "theatres", key = "#id")
    public Theatre updateTheatre(String id, TheatreRequest request) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Theatre not found with ID: " + id));
        theatre.setName(request.name());
        theatre.setLocationId(request.locationId());
        theatre.setAddress(request.address());
        return theatreRepository.save(theatre);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "theatres", key = "#id")
    public Theatre approveTheatre(String id, String status) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Theatre not found with ID: " + id));
        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new CatalogException("Invalid approval status. Must be APPROVED or REJECTED.");
        }
        theatre.setStatus(status.toUpperCase());
        return theatreRepository.save(theatre);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "theatres", key = "#id")
    public void deleteTheatre(String id) {
        if (!theatreRepository.existsById(id)) {
            throw new CatalogException("Theatre not found with ID: " + id);
        }
        theatreRepository.deleteById(id);
    }

    @org.springframework.cache.annotation.Cacheable(value = "theatres", key = "#id")
    public Theatre getTheatreById(String id) {
        return theatreRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Theatre not found with ID: " + id));
    }

    public List<Theatre> getTheatresByLocation(String locationId, String status) {
        if (status != null && !status.isEmpty()) {
            return theatreRepository.findByLocationIdAndStatus(locationId, status.toUpperCase());
        }
        return theatreRepository.findByLocationId(locationId);
    }

    public List<Theatre> getAllTheatres(String status) {
        if (status != null && !status.isEmpty()) {
            return theatreRepository.findByStatus(status.toUpperCase());
        }
        return theatreRepository.findAll();
    }
}
