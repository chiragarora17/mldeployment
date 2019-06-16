package com.imageclassification.server

import com.imageclassification.service.ImageServiceDescriptor
import com.proton.config.GlobalConfigModule
import com.proton.http.server.{HttpServer, HttpServiceBuilder}
import com.proton.module.InjectorBuilder

object Main extends App {

  val globalConfigModule = new GlobalConfigModule
  val configInjector = new InjectorBuilder().addServiceDescriptors().addModules(globalConfigModule)
    .build()

  def server: HttpServer = new HttpServiceBuilder(ImageServiceDescriptor.get())
    .routes(classOf[ImageServiceRouteV1])
    .port(9130)
    .build()

  server.start()
}
