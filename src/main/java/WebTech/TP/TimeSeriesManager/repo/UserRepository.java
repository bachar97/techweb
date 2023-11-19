package WebTech.TP.TimeSeriesManager.repo;

import WebTech.TP.TimeSeriesManager.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}