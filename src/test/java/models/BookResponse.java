package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponse {
  private String isbn;
  private String title;
  private String subTitle;
  private String author;

  @JsonProperty("publish_date")
  private String publishDate;

  private String publisher;
  private int pages;
  private String description;
  private String website;
}
