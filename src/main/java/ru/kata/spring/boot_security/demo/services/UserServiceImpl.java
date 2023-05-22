package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepositories;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepositories userRepositories;

    public UserServiceImpl(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;

    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String email) {
        return userRepositories.findByUsername(email);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        //метод для сохранения данных пользователя при редактировании
//        if (getUserByEmail(user.getEmail()) != null) {
//            throw new RuntimeException("Пользователь с таким логином уже существует");
//        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepositories.save(user);
    }
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepositories.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepositories.findById(id);
    }

    @Override
    @Transactional
    public void adminRedactor(User user, Long id) {
        Optional<User> optionalUser = userRepositories.findById(id);
        if (optionalUser.isPresent()) {
            User editUser = optionalUser.get();
            editUser.setId(user.getId());
            BeanUtils.copyProperties(user, editUser, "password");
            if (!editUser.getPassword().equals(user.getPassword())) {
                editUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            }
            userRepositories.save(editUser);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepositories.getUserByEmail(email);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public User getUserByLogin(String login) {
//        return userRepositories.getUserByLogin(login);
//    }

    @Override
    public User findOne(Long id) {
        return userRepositories.findById(id).get();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepositories.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByUsername(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
