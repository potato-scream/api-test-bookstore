package models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AddBooksRequest {
  private String userId;
  private List<IsbnItem> collectionOfIsbns;

  @Data
  @AllArgsConstructor
  public static class IsbnItem {
    private String isbn;
  }
}
