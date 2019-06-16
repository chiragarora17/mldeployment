package com.imageclassification.server

import javax.inject.Inject

import com.image.api.ImageClassificationService
import com.proton.http.server.{HttpService, Response}

class ImageServiceRouteV1 @Inject()(service: ImageClassificationService)
  extends HttpService() {

  post("v1", "/predict") { request => {
    val path = request.getPathParams.get("path")
    if (path == null) {
      Response.error("Could not find path for the uploaded file")
    }
    Response.ok(service.getImageLabels(path))
  }
  }

  get("/getstatus") { request =>
    Response.ok(service.getModelInfo)
  }
}
