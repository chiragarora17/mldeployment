package com.imageclassification.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.image.api.ImageClassificationService;
import com.image.api.ImageResponse;
import com.image.api.ModelResponse;
import com.proton.module.ServiceResponse;

/**
 * @author carora
 * @since 6/15/19
 */
public class ImageClassificationServiceImpl implements ImageClassificationService {

  private final Logger log = LogManager.getLogger(ImageClassificationServiceImpl.class);
  private final Prediction predictor;

  public ImageClassificationServiceImpl(Prediction predictor) {
    this.predictor = predictor;
  }

  @Override
  public ServiceResponse<ImageResponse> getImageLabels(String url) {
    File file = new File(url);
    if (!file.exists()) {
      throw new RuntimeException("File does not exists at :" + url);
    }
    List<String> labels = new ArrayList<>();
    try {
      labels.addAll(predictor.predictions(file));
    } catch (IOException e) {
      log.error("file read failed {}" + file.getAbsolutePath());
      throw new RuntimeException("file read failed " + file.getAbsolutePath());
    }
    ImageResponse s = new ImageResponse(new HashSet<>(labels));
    return ServiceResponse.now(s);
  }

  @Override
  public ServiceResponse<ModelResponse> getModelInfo() {
    return ServiceResponse.now(new ModelResponse(new Date().toString(), "1.0", predictor.getModelInfo()));
  }
}
