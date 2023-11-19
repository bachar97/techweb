package WebTech.TP.TimeSeriesManager.dao;

import WebTech.TP.TimeSeriesManager.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@Builder
@Table(name = "USERS")
public class User {
    @Id
    private String username;
    private String password;

    public UserDTO toDTO() {
        return new UserDTO(username);
    }
}
