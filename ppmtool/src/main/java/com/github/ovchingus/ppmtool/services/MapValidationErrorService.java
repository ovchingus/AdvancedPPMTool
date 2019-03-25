package com.github.ovchingus.ppmtool.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MapValidationErrorService {

    public Optional<ResponseEntity<?>> MapValidationService(BindingResult result) {
        if (result.hasErrors()) {
            return Optional.of(new ResponseEntity<>(result.getFieldErrors().stream().collect(Collectors.toMap(
                    o -> o.getField(),
                    o -> o.getDefaultMessage(),
                    (firstError, otherErrors) -> firstError // ignore adding the same key to map
                    )), HttpStatus.BAD_REQUEST));
        }
        return Optional.empty();

    }
}
