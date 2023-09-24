package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Builder
@ToString
public class UserDTO {
    private UUID id;
    private String name;
    private String phoneNumber;
    private int age;
}
