package com.example.resultbot.service;

import com.example.resultbot.entity.User;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.UserRepository;
import com.example.resultbot.service.dto.UserDto;
import com.example.resultbot.service.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        if (userDto.getStatus() == null) {
            user.setStatus(Status.ACTIVE);
        }
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto update(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public List<UserDto> findAllUser() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public User findById(Long id) {
        return userRepository
                .findById(id)
                .orElseGet(User::new);
    }

    public User delete(Long id) {
        return userRepository.updateStatus(id, Status.DELETE);
    }


}
