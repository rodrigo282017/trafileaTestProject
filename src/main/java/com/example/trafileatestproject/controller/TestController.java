package com.example.trafileatestproject.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * This controller is only created for configuration purposes. But it
 * will be deleted.
 */

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void testMethod() {}
}
