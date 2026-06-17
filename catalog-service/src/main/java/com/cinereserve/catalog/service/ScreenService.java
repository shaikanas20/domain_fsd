package com.cinereserve.catalog.service;

import com.cinereserve.catalog.dto.ScreenRequest;
import com.cinereserve.catalog.exception.CatalogException;
import com.cinereserve.catalog.model.Screen;
import com.cinereserve.catalog.repository.ScreenRepository;
import com.cinereserve.catalog.repository.SeatLayoutRepository;
import com.cinereserve.catalog.repository.TheatreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final SeatLayoutRepository seatLayoutRepository;

    public ScreenService(ScreenRepository screenRepository,
                         TheatreRepository theatreRepository,
                         SeatLayoutRepository seatLayoutRepository) {
        this.screenRepository = screenRepository;
        this.theatreRepository = theatreRepository;
        this.seatLayoutRepository = seatLayoutRepository;
    }

    public Screen createScreen(ScreenRequest request) {
        if (!theatreRepository.existsById(request.theatreId())) {
            throw new CatalogException("Cannot create screen. Theatre not found with ID: " + request.theatreId());
        }
        if (!seatLayoutRepository.existsById(request.seatLayoutId())) {
            throw new CatalogException("Cannot create screen. Seat layout not found with ID: " + request.seatLayoutId());
        }

        Screen screen = Screen.builder()
                .theatreId(request.theatreId())
                .name(request.name())
                .seatLayoutId(request.seatLayoutId())
                .build();
        return screenRepository.save(screen);
    }

    public Screen updateScreen(String id, ScreenRequest request) {
        Screen screen = screenRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Screen not found with ID: " + id));

        if (!theatreRepository.existsById(request.theatreId())) {
            throw new CatalogException("Cannot update screen. Theatre not found with ID: " + request.theatreId());
        }
        if (!seatLayoutRepository.existsById(request.seatLayoutId())) {
            throw new CatalogException("Cannot update screen. Seat layout not found with ID: " + request.seatLayoutId());
        }

        screen.setName(request.name());
        screen.setTheatreId(request.theatreId());
        screen.setSeatLayoutId(request.seatLayoutId());

        return screenRepository.save(screen);
    }

    public void deleteScreen(String id) {
        if (!screenRepository.existsById(id)) {
            throw new CatalogException("Screen not found with ID: " + id);
        }
        screenRepository.deleteById(id);
    }

    public Screen getScreenById(String id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Screen not found with ID: " + id));
    }

    public List<Screen> getScreensByTheatre(String theatreId) {
        return screenRepository.findByTheatreId(theatreId);
    }
}
