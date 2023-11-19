package WebTech.TP.TimeSeriesManager.config;

import WebTech.TP.TimeSeriesManager.dao.User;
import WebTech.TP.TimeSeriesManager.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        this.userService.save(new User("admin", "admin"));
    }
}
