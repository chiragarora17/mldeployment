package com.imageclassification.service;

import javax.inject.Inject;

import com.image.api.ImageClassificationService;
import com.proton.module.ServiceProvider;

public class ImageServiceProvider extends ServiceProvider<ImageClassificationService> {

  private final ImageClassificationService service;

  @Inject
  public ImageServiceProvider(Prediction predictor) {
    this.service = new ImageClassificationServiceImpl(predictor);
  }

  @Override
  public ImageClassificationService get() {
    return service;
  }

}
