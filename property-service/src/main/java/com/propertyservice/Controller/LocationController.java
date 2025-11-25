package com.propertyservice.Controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.propertyservice.Entity.Area;
import com.propertyservice.Entity.City;
import com.propertyservice.Entity.State;
import com.propertyservice.Payload.APIResponse;
import com.propertyservice.Repository.AreaRepository;
import com.propertyservice.Repository.CityRepository;
import com.propertyservice.Repository.StateRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AreaRepository areaRepository;

    @PostMapping("/state")
    public ResponseEntity<APIResponse<State>> addState(@RequestBody State state) {
        State savedState = stateRepository.save(state);
        APIResponse<State> response = new APIResponse<>();
        response.setMessage("State added successfully");
        response.setStatus(201);
        response.setData(savedState);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/city")
    public ResponseEntity<APIResponse<City>> addCity(@RequestBody City city) {
        City savedCity = cityRepository.save(city);
        APIResponse<City> response = new APIResponse<>();
        response.setMessage("City added successfully");
        response.setStatus(201);
        response.setData(savedCity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/area")
    public ResponseEntity<APIResponse<Area>> addArea(@RequestBody Area area) {
        Area savedArea = areaRepository.save(area);
        APIResponse<Area> response = new APIResponse<>();
        response.setMessage("Area added successfully");
        response.setStatus(201);
        response.setData(savedArea);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/states")
    public ResponseEntity<APIResponse<List<State>>> getAllStates() {
        List<State> states = stateRepository.findAll();
        APIResponse<List<State>> response = new APIResponse<>();
        response.setMessage("States retrieved successfully");
        response.setStatus(200);
        response.setData(states);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cities")
    public ResponseEntity<APIResponse<List<City>>> getAllCities() {
        List<City> cities = cityRepository.findAll();
        APIResponse<List<City>> response = new APIResponse<>();
        response.setMessage("Cities retrieved successfully");
        response.setStatus(200);
        response.setData(cities);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/areas")
    public ResponseEntity<APIResponse<List<Area>>> getAllAreas() {
        List<Area> areas = areaRepository.findAll();
        APIResponse<List<Area>> response = new APIResponse<>();
        response.setMessage("Areas retrieved successfully");
        response.setStatus(200);
        response.setData(areas);
        return ResponseEntity.ok(response);
    }
}
