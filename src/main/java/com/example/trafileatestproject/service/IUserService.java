package com.example.trafileatestproject.service;

import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.api.UserDTO;

import java.util.List;

public interface IUserService {
    List<UserDTO> getAllUsers();

    UserDTO getUserById(String id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateProduct(String id, UserDTO userDTO);

    void deleteUser(String id);
}
