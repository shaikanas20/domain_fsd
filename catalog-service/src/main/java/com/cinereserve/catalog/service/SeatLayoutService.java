package com.cinereserve.catalog.service;

import com.cinereserve.catalog.dto.SeatLayoutRequest;
import com.cinereserve.catalog.exception.CatalogException;
import com.cinereserve.catalog.model.SeatLayout;
import com.cinereserve.catalog.repository.SeatLayoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatLayoutService {

    private final SeatLayoutRepository seatLayoutRepository;

    public SeatLayoutService(SeatLayoutRepository seatLayoutRepository) {
        this.seatLayoutRepository = seatLayoutRepository;
    }

    public SeatLayout createSeatLayout(SeatLayoutRequest request) {
        List<SeatLayout.SeatConfig> seats = request.seats().stream()
                .map(dto -> SeatLayout.SeatConfig.builder()
                        .rowName(dto.rowName())
                        .colIndex(dto.colIndex())
                        .seatType(dto.seatType())
                        .build())
                .collect(Collectors.toList());

        SeatLayout seatLayout = SeatLayout.builder()
                .name(request.name())
                .rowNames(request.rowNames())
                .colCount(request.colCount())
                .seats(seats)
                .build();

        return seatLayoutRepository.save(seatLayout);
    }

    public SeatLayout getSeatLayoutById(String id) {
        return seatLayoutRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Seat layout not found with ID: " + id));
    }

    public List<SeatLayout> getAllSeatLayouts() {
        return seatLayoutRepository.findAll();
    }

    public void deleteSeatLayout(String id) {
        if (!seatLayoutRepository.existsById(id)) {
            throw new CatalogException("Seat layout not found with ID: " + id);
        }
        seatLayoutRepository.deleteById(id);
    }
}
