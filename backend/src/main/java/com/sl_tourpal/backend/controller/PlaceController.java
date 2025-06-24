package com.sl_tourpal.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sl_tourpal.backend.service.PlaceService;
import com.sl_tourpal.backend.domain.Place;
import com.sl_tourpal.backend.dto.PlaceDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/getAllPlace")
    public List<PlaceDTO> getAllPlaces() {
        return placeService.getAllPlaces()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getPlace/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable Long id) {
        return placeService.getPlaceById(id)
                .map(place -> ResponseEntity.ok(toDTO(place)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/addPlace")
    public PlaceDTO createPlace(@RequestBody PlaceDTO placeDTO) {
        Place place = toEntity(placeDTO);
        Place savedPlace = placeService.createPlace(place);
        return toDTO(savedPlace);
    }

    @PutMapping("/updatePlace/{id}")
    public ResponseEntity<PlaceDTO> updatePlace(@PathVariable Long id, @RequestBody PlaceDTO placeDTO) {
        try {
            Place updatedPlace = placeService.updatePlace(id, toEntity(placeDTO));
            return ResponseEntity.ok(toDTO(updatedPlace));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletePlace/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.noContent().build();
    }

    // Helper methods to convert between Place and PlaceDTO
    private PlaceDTO toDTO(Place place) {
        PlaceDTO dto = new PlaceDTO();
        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setRegion(place.getRegion());
        return dto;
    }

    private Place toEntity(PlaceDTO dto) {
        Place place = new Place();
        place.setId(dto.getId());
        place.setName(dto.getName());
        place.setRegion(dto.getRegion());
        return place;
    }
}
