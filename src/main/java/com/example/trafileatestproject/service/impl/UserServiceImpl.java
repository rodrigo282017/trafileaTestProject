package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.exceptions.EntityNotFoundException;
import com.example.trafileatestproject.exceptions.ValidationException;
import com.example.trafileatestproject.model.api.UserDTO;
import com.example.trafileatestproject.model.entity.User;
import com.example.trafileatestproject.model.mapper.UserMapper;
import com.example.trafileatestproject.repository.UserRepository;
import com.example.trafileatestproject.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> user = userRepository.findAll();

        return new UserMapper().toDTOs(user);
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("User not found", "Could not find user.", id));

        return new UserMapper().toDto(user);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        validateRequest(userDTO);
        User user  = userRepository.save(new UserMapper().toEntity(userDTO));
        userDTO.setId(user.getId());

        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(id));
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found", "Could not find user.", id);
        }

        User user = optionalUser.get();

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }

        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (userDTO.getAge() > 18) {
            user.setAge(userDTO.getAge());
        }

        userRepository.save(user);

        return new UserMapper().toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }

    private void validateRequest(UserDTO userDTO) {
        if (!hasText(userDTO.getName()) || userDTO.getPhoneNumber() == null || userDTO.getAge() < 18) {
            throw new ValidationException(
                    "MissingRequiredParameters",
                    "Name, phone number and age higher than 18 are required",
                    userDTO.getName(),
                    userDTO.getPhoneNumber()
            );
        }
    }
}
