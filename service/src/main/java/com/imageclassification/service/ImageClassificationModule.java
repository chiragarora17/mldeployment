package com.imageclassification.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.model.TinyYOLO;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;

/**
 * @author carora
 * @since 6/16/19
 */
public class ImageClassificationModule extends AbstractModule {

  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  public ComputationGraph providesModel() throws IOException {
    ComputationGraph model = (ComputationGraph) TinyYOLO.builder().build().initPretrained();
    model.init();
    return model;
  }

  @Provides
  @Singleton
  public List<String> provideLabels(Config config) throws IOException {
    String file = config.getString("pathToLabels");
    BufferedReader reader = new BufferedReader(new FileReader(file));
    List<String> labels = new ArrayList<String>();
    String line;
    while((line = reader.readLine()) != null){
      labels.add(line);
    }
    reader.close();
    return labels;
  }

}
