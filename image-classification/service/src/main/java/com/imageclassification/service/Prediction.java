package com.imageclassification.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

import com.typesafe.config.Config;


/**
 * @author carora
 * @since 6/16/19
 */
public class Prediction {
  private static final Logger log = LogManager.getLogger(Prediction.class);

  private final Config config;
  private int width;
  private int height = 416;
  private int nChannels = 3;
  private int gridWidth = 13;
  private int gridHeight = 13;
  private double detectionThreshold;
  private final ComputationGraph model;
  private final DataNormalization scaler;
  private final NativeImageLoader loader;
  private final String summary;
  private final List<String> labels;

  @Inject
  public Prediction(Config config, ComputationGraph model, List<String> labels) {
    this.config = config;
    width = config.getInt("width");
    height = config.getInt("height");
    nChannels = config.getInt("nChannels");
    gridWidth = config.getInt("gridWidth");
    gridHeight = config.getInt("gridHeight");
    detectionThreshold = config.getDouble("detectionThreshold");
    this.model = model;
    this.summary = model.summary();
    scaler = new ImagePreProcessingScaler(0, 1);
    loader = new NativeImageLoader(height, width, nChannels);
    this.labels = labels;
  }

  public String getModelInfo() {
    return summary;
  }

  public List<String> predictions(File file) throws IOException {
    //File file = new File("/Users/carora/Downloads/person.jpg");
    INDArray ima = loader.asMatrix(new FileInputStream(file));
    scaler.transform(ima);
    INDArray output = model.outputSingle(false, ima);
    //System.out.println(output.getDouble(0));

    Yolo2OutputLayer lastLayer = (Yolo2OutputLayer) model.getOutputLayer(0);
    List<DetectedObject> detectedObjects = lastLayer.getPredictedObjects(output, detectionThreshold);
    return detectedObjects.stream().map(r -> labels.get(r.getPredictedClass()))
      .collect(Collectors.toList());
  }
}
