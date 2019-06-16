package com.imageclassification.service;

import com.proton.json.JsonModule;
import com.proton.module.ServiceDescriptor;
import com.proton.module.ServiceName;

public class ImageServiceDescriptor {

  public static ServiceDescriptor get() {
    return new ServiceDescriptor.Builder(ServiceName.of("imageclassification"), ImageServiceProvider.class)
      .addModules(new JsonModule())
      .addModules(new ImageClassificationModule())
      .build();
  }
}
