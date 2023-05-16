package ru.kata.spring.boot_security.demo.services;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepositories;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleRepositories roleRepositories;

    public RoleServiceImpl(RoleRepositories roleRepositories) {
        this.roleRepositories = roleRepositories;
    }
    @Override
    public List<Role> findAll() {
        return roleRepositories.findAll();
    }


}
