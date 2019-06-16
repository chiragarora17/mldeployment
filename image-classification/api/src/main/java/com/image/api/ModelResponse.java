package com.image.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author carora
 * @since 6/15/19
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelResponse {

  private final String currentTime;
  private final String modelVersion;
  private final String modelSummary;

  @JsonCreator
  public ModelResponse(@JsonProperty("currentTime") String currentTime, @JsonProperty("modelVersion") String modelVersion,
                       @JsonProperty("modelSummary") String modelSummary) {
    this.modelVersion = modelVersion;
    this.currentTime = currentTime;
    this.modelSummary = modelSummary;
  }

  public String getCurrentTime() {
    return currentTime;
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public String getModelSummary() {
    return modelSummary;
  }

  @Override
  public String toString() {
    return "ModelResponse{"
      + "currentTime='" + currentTime + '\''
      + ", modelVersion='" + modelVersion + '\''
      + ", modelSummary='" + modelSummary + '\''
      + '}';
  }
}
