# Apple ML Deployment Challenge

**Chirag Arora**

Table of contents
=================

   * [User Story](#user-story)
   * [Components](#components)
      * [Model Selection](#model-selection)
      * [Framework](#framework)
      * [Server](#server)
   * [Architecture](#architecture)
   * [Instructions](#instructions)
   * [API](#api)
   * [Improvements](#improvements)
   * [Alternatives](#alternatives)
      * [TensorFlow](#tensorflow) (TensorFlow Serving)
      * [MapDB](#mapdb)
   * [Screenshots](#screenshots)

## User Story
Data scientist has asked to deploy any selected model and deploy it on AWS instance using any pre-installed AMI image. Also write the HTTP server to handle the requests to analyze the image against the model. 

## Components
1. ##### Model Selection 

For image labels classification, I thought of any model which supports object detection. One of the best pre-trained model for object detection is YOLO (You only train once). Its faster than fasterCNN. I decided to choose TinyYoloV2 because of past introductory experience and its easy to get its pre-trained model. I choose pre-trained model , trained on VOC dataset with 19 classes.
https://github.com/pjreddie/darknet/blob/master/

2. ##### Framework

Since it was given, I can choose my choice of framework. I decided to go with DeepLearning4J (https://deeplearning4j.org) Framework instead of TensorFlow or PyTorch because of its support in Java. If I had to train myself, then I would have used TF. But for inference, I will use either DL4j or TF. 
 - I could have deployed a Flask server / microservice interacting with Tensorflow Serving for inference but sometimes python / flask server is not faster for production level requests. 
 - Therefore, I choose Java and thus DL4J (due to past experience with ND4J, I prefer this framework)
 - This framework does have some limitation in terms of features in comparision to TF but for my purpose (providing inferences from YOLO model) it was the quickest (I thought that initially) approach.
 
2. ##### Server

- I used the microservice framework I built around 2-3 years ago inspired from Netflix Governator and Nike's riposte (based of Netty). It was old code base and I had to change my channels to accept File Uploads in the basic framework. But framework does handle exceptions and unknown routes properly. Some changes to the framework and then microservice.
- Using this framework, a microservice "Image Classification service" has been deployed on AWS docker container which has two endpoints as mentioned in the user requirements. If user does make calls to other endpoints, it will show a standard 404 error. 

NOTE: AWS micro AMI instance was used per instructions, though building of docker container was build locally to save time. 

## Architecture


## Instructions
1. On docker hub, one can download either a container which has model, dl framework and server as a whole package (created via DockerFile) 
2. Or, one can create their locally from the docker file itself. 

Docker file creates an image by 
- copying the model to the container
- installing all the dependencies via maven (downloading the DL4J framework)
- copying the data label file
- copying all the other configuration related to the netty server such as where is the location of the label file. 

Here are 2 Docker files used to create an image and run the docker container.

To pull the docker container: 

``` docker pull chiragarora17/ml_deployment:0.1 ```


To Run the docker container:

``` docker run -d -p 9130:9130 chiragarora17/ml_deployment:latest  ```



Alternativelly, to run the jar directly, once can use

``` java  -server -Xms256m -Xmx1024m -XX:+UseParallelOldGC -Dlog4j.configurationFile=log4j.properties -cp config:imageclassification-server-master-SNAPSHOT-withdeps.jar com.imageclassification.server.Main```
where config has the application.conf file and voc.names label file it.

## API

![Alt text](screenshots/table.png?raw=true "API  Table")

During the bootstrap, server might takes 2-3 minutes because of its loading the model and performing its init sequence.
To interact with API, user needs to make an api call with an image. 
- Make call using any rest client, to ```/predict``` api and the response will have the labels with 200 OK response 

```json
{
    "labels": [
        "dog"
    ]
}
```

- Get ```/getstatus``` call returns the current timestamp, with model version, and model summary (like how many networks)

```json
{
    "currentTime": "Sun Jun 16 19:18:18 UTC 2019",
    "modelVersion": "1.0",
    "modelSummary": "\n===========================================================================================================================================================\nVertexName (VertexType)                      nIn,nOut    TotalParams   ParamsShape                                                  Vertex Inputs          \n===========================================================================================================================================================\ninput_1 (InputVertex)                        -,-         -             -                                                            -                      \nconv2d_1 (ConvolutionLayer)                  3,16        432           W:{16,3,3,3}                                                 [input_1]              \nbatch_normalization_1 (BatchNormalization)   16,16       64            gamma:{1,16}, beta:{1,16}, mean:{1,16}, var:{1,16}           [conv2d_1]             \nleaky_re_lu_1 (ActivationLayer)              -,-         0             -                                                            [batch_normalization_1]\nmax_pooling2d_1 (SubsamplingLayer)           -,-         0             -                                                            [leaky_re_lu_1]        \nconv2d_2 (ConvolutionLayer)                  16,32       4608          W:{32,16,3,3}"
}
```

- On wrong url, user will get ```404 Error```
```json
{
    "code": 404,
    "details": "The requested URL was not found on the server.",
    "message": "Not Found",
    "status": "error"
}
```

- On errors occurances, users will get a standard error message with either ```500 internal error``` or ```400 bad request```
  - Bad Request can occur, when user didn't send a file
  
  - While processing a file, which is to upload to a temp directory and then performance inference, if something goes wrong during this process, then server will end up throwing ```500 internal error``` with stack trace
  



## Improvements
Given limited time, User story was completed successfully. But I wanted to add some more automation around it, so I tried to automate the whole flow from WorkFlow that is checking out Git and spining docker container. Although a bit automation of pipeline is done by Docker files to redeploy the server, Apache Worflow is better. This can be expanded later on.

![Alt text](screenshots/workflow.png?raw=true "Workflow")


## Alternatives
1. ##### Tensorflow

Another deployment as discussed before could have been with TensorFlow, using Flask service on top. Getting flask to run and integrating TF is easy. The integration between Python with TF Serving is very smooth. 

2. ##### MapDB

Another infereces can be done through MapDB or basically in memory KV store. For this we need to create a in memory store MapDB file and dump all the feature vectors information. Depending on the calculations, it can be tricky. I used this route previously in my recent past experience but its not that useful.

## Screenshots

##### Dog Prediction using one of the example images
![Alt text](screenshots/dog_prediction_result.png?raw=true "Dog Prediction using one of the example images")

##### Get Status 
![Alt text](screenshots/getstatus_result.png?raw=true "Get status api result")

##### ERROR On POST (No File Provided)
![Alt text](screenshots/no_file_post_error_result.png?raw=true "POST ERROR")


##### Wrong URL on POST
![Alt text](screenshots/wrong_url_post_result.png?raw=true "Wrong URL on POST")

##### Wrong URL on Get
![Alt text](screenshots/wrongurl_get_result.png?raw=true "Wrong URL on GET")

##### Running server on docker aws
![Alt text](screenshots/aws_docker.png?raw=true "Running server on docker aws")
