package WebTech.TP.TimeSeriesManager.service.impl;

import WebTech.TP.TimeSeriesManager.dao.MyUserDetails;
import WebTech.TP.TimeSeriesManager.dao.User;
import WebTech.TP.TimeSeriesManager.repo.UserRepository;
import WebTech.TP.TimeSeriesManager.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public void save(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findById(username)
                .map(user -> new MyUserDetails(user))
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Username %s was not found", username)
                ));
    }
}
