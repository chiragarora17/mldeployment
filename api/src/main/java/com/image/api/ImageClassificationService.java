package com.image.api;

import com.proton.module.ServiceResponse;

/**
 * @author carora
 * @since 6/15/19
 */
public interface ImageClassificationService {

  /**
   * return the labels of the image passed
   */
  ServiceResponse<ImageResponse> getImageLabels(String url);

  /**
   * return the model info
   */
  ServiceResponse<ModelResponse> getModelInfo();

}
