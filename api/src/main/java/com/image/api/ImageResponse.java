package com.image.api;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author carora
 * @since 6/15/19
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponse {

  private final Set<String> labels;

  @JsonCreator
  public ImageResponse(@JsonProperty("labels") Set<String> labels) {
    this.labels = labels;
  }

  public Set<String> getLabels() {
    return labels;
  }

  @Override
  public String toString() {
    return "ImageResponse{"
      + "labels=" + labels
      + '}';
  }
}
