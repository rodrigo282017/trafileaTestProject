package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.model.api.UserDTO;
import com.example.trafileatestproject.model.entity.User;
import com.example.trafileatestproject.model.mapper.UserMapper;
import com.example.trafileatestproject.repository.UserRepository;
import com.example.trafileatestproject.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> user = userRepository.findAll();

        return new UserMapper().toDtos(user);
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("User not found for this id: " + id));

        return new UserMapper().toDto(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user  = userRepository.save(new UserMapper().toEntity(userDTO));
        userDTO.setId(user.getId());

        return userDTO;
    }

    @Override
    public UserDTO updateProduct(String id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(id));

        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found for this id: " + id);
        }

        User user = optionalUser.get();

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }

        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }

        userRepository.save(user);

        return new UserMapper().toDto(user);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }
}
