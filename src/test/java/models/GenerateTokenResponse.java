/* (C) 2025 potato-scream */
package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateTokenResponse {
  private String token;
  private String expires;
  private String status;
  private String result;
}
