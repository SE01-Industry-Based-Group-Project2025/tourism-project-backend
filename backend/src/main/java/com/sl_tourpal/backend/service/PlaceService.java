package com.sl_tourpal.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sl_tourpal.backend.domain.Place;
import com.sl_tourpal.backend.repository.PlaceRepository;

@Service
public class PlaceService {
    
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }
    public List<Place> getAllPlaces(){
        return placeRepository.findAll();
    }

    public Optional<Place> getPlaceById(Long id){
        return placeRepository.findById(id);
    }

    public Place createPlace(Place place){
        return placeRepository.save(place);
    }

    public Place updatePlace(Long id , Place updatedPlace){
        Place existing = placeRepository.findById(id).orElseThrow();
        existing.setName(updatedPlace.getName());
        existing.setRegion(updatedPlace.getRegion());
        return placeRepository.save(existing);
    }

    public void deletePlace(Long id){
        placeRepository.deleteById(id);
    }

}
