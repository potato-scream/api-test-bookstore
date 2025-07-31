/* (C) 2025 potato-scream */
package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
  private String userId;
  private String token;
  private String username;
  private String password;
  private String expires;
  private String created_date;
}
