package WebTech.TP.TimeSeriesManager.service;

import WebTech.TP.TimeSeriesManager.dao.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(String id);

    boolean existsById(String id);

    void save(User event);

    void deleteById(String id);
}
