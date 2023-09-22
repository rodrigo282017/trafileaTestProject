package com.example.trafileatestproject.model.mapper;

import com.example.trafileatestproject.model.api.UserDTO;
import com.example.trafileatestproject.model.entity.User;
import com.example.trafileatestproject.util.Mapper;

import java.util.List;

public class UserMapper implements Mapper<UserDTO, User> {

    @Override
    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .age(user.getAge())
                .build();
    }

    @Override
    public List<UserDTO> toDtos(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .phoneNumber(userDTO.getPhoneNumber())
                .age(userDTO.getAge())
                .build();
    }
}
