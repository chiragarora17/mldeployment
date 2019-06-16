package com.imageclassification.service;

import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_DUPLEX;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.datavec.api.records.metadata.RecordMetaDataImageURI;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.zoo.model.TinyYOLO;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

/**
 * @author carora
 * @since 6/16/19
 */
public class Training {

  private static final Logger log = LogManager.getLogger(Prediction.class);

  public static void main(String[] args) throws java.lang.Exception {

    // parameters matching the pretrained TinyYOLO model
    int width = 416;
    int height = 416;
    int nChannels = 3;
    int gridWidth = 13;
    int gridHeight = 13;

    // number classes (digits) for the SVHN datasets
    int nClasses = 10;

    // parameters for the Yolo2OutputLayer
    int nBoxes = 5;
    double lambdaNoObj = 0.5;
    double lambdaCoord = 1.0;
    double[][] priorBoxes = {{2, 5}, {2.5, 6}, {3, 7}, {3.5, 8}, {4, 9}};
    double detectionThreshold = 0.5;

    // parameters for the training phase
    int batchSize = 10;
    int nEpochs = 20;
    double learningRate = 1e-4;
    double lrMomentum = 0.9;

    int seed = 123;
    Random rng = new Random(seed);

    //SvhnDataFetcher fetcher = new SvhnDataFetcher();
    //File trainDir = fetcher.getDataSetPath(DataSetType.TRAIN);
    //File testDir = fetcher.getDataSetPath(DataSetType.TEST);


    log.info("Load data...");


    RecordReaderDataSetIterator train = null;
    RecordReaderDataSetIterator test = null;
    //String filename = "/Users/carora/Downloads/tiny-yolo-voc.h5";
    ComputationGraph model = (ComputationGraph) TinyYOLO.builder().build().initPretrained();
    model.init();
    System.out.println(model.summary());
    NativeImageLoader loader = new NativeImageLoader(height, width, nChannels);
    File file = new File("/Users/carora/Downloads/person.jpg");
    INDArray ima = loader.asMatrix(new FileInputStream(file));
    //new VGG16ImagePreProcessor()
    DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
    scaler.transform(ima);
    INDArray output = model.outputSingle(false, ima);
    System.out.println(output.getDouble(0));

    //VocLabelProvider vocLabelProvider = new VocLabelProvider();
    //vocLabelProvider.getImageObjectsForPath()
    Yolo2OutputLayer lastLayer =
      (Yolo2OutputLayer) model.getOutputLayer(0);
    List<DetectedObject> hope = lastLayer.getPredictedObjects(output, detectionThreshold);
    log.info(file.getName() + ": " + hope);

    for (DetectedObject obj : hope) {
      double[] xy1 = obj.getTopLeftXY();
      double[] xy2 = obj.getBottomRightXY();
      System.out.println(obj.getPredictedClass());
      //String label = labels.get(obj.getPredictedClass());
      //System.out.println(label);
      //int x1 = (int) Math.round(w * xy1[0] / gridWidth);
      //int y1 = (int) Math.round(h * xy1[1] / gridHeight);
      //int x2 = (int) Math.round(w * xy2[0] / gridWidth);
      //int y2 = (int) Math.round(h * xy2[1] / gridHeight);
      //rectangle(image, new Point(x1, y1), new Point(x2, y2), Scalar.RED);
      //putText(image, label, new Point(x1 + 2, y2 - 2), FONT_HERSHEY_DUPLEX, 1, Scalar.GREEN);
    }

    //model.outputSingle()

    //ComputationGraph model = (ComputationGraph) TinyYOLO.builder().build().initPretrained();;
    String modelFilename = "model.zip";

    if (true) {
      //log.info("Load model...");
      //
      ////model = ComputationGraph.load(new File(modelFilename), true);
      //ComputationGraph mm = (ComputationGraph) TinyYOLO.builder().build().initPretrained();
      //INDArray priors = Nd4j.create(priorBoxes);
      //Yolo2OutputLayer yout =
      //  (Yolo2OutputLayer) model.getOutputLayer(0);
      ////List<String> labels = train.getLabels();
      //mm.outputSingle();

    } else {
      //log.info("Build model...");
      //
      //ComputationGraph pretrained = (ComputationGraph)TinyYOLO.builder().build().initPretrained();
      //INDArray priors = Nd4j.create(priorBoxes);
      //
      //FineTuneConfiguration fineTuneConf = new FineTuneConfiguration.Builder()
      //  .seed(seed)
      //  .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      //  .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
      //  .gradientNormalizationThreshold(1.0)
      //  .updater(new Adam.Builder().learningRate(learningRate).build())
      //  //.updater(new Nesterovs.Builder().learningRate(learningRate).momentum(lrMomentum).build())
      //  .l2(0.00001)
      //  .activation(Activation.IDENTITY)
      //  .trainingWorkspaceMode(WorkspaceMode.SEPARATE)
      //  .inferenceWorkspaceMode(WorkspaceMode.SEPARATE)
      //  .build();

      //model = new TransferLearning.GraphBuilder(pretrained)
      //  .fineTuneConfiguration(fineTuneConf)
      //  .removeVertexKeepConnections("conv2d_9")
      //  .removeVertexKeepConnections("outputs")
      //  .addLayer("convolution2d_9",
      //    new ConvolutionLayer.Builder(1,1)
      //      .nIn(1024)
      //      .nOut(nBoxes * (5 + nClasses))
      //      .stride(1,1)
      //      .convolutionMode(ConvolutionMode.Same)
      //      .weightInit(WeightInit.XAVIER)
      //      .activation(Activation.IDENTITY)
      //      .build(),
      //    "leaky_re_lu_8")
      //  .addLayer("outputs",
      //    new Yolo2OutputLayer.Builder()
      //      .lambbaNoObj(lambdaNoObj)
      //      .lambdaCoord(lambdaCoord)
      //      .boundingBoxPriors(priors)
      //      .build(),
      //    "convolution2d_9")
      //  .setOutputs("outputs")
      //  .build();
      //System.out.println(model.summary(InputType.convolutional(height, width, nChannels)));


      //log.info("Train model...");
      //
      //model.setListeners(new ScoreIterationListener(1));
      //model.fit(train, nEpochs);
      //
      //log.info("Save model...");
      //ModelSerializer.writeModel(model, modelFilename, true);


      // visualize results on the test set
      NativeImageLoader imageLoader = new NativeImageLoader();
      CanvasFrame frame = new CanvasFrame("HouseNumberDetection");
      OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
      Yolo2OutputLayer yout =
        (Yolo2OutputLayer) model.getOutputLayer(0);

      List<String> labels = train.getLabels();
      test.setCollectMetaData(true);
      while (test.hasNext() && frame.isVisible()) {
        org.nd4j.linalg.dataset.DataSet ds = test.next();
        RecordMetaDataImageURI metadata = (RecordMetaDataImageURI) ds.getExampleMetaData().get(0);
        INDArray features = ds.getFeatures();
        INDArray results = model.outputSingle(features);
        List<DetectedObject> objs = yout.getPredictedObjects(results, detectionThreshold);
        file = new File(metadata.getURI());
        log.info(file.getName() + ": " + objs);

        Mat mat = imageLoader.asMat(features);
        Mat convertedMat = new Mat();
        mat.convertTo(convertedMat, CV_8U, 255, 0);
        int w = metadata.getOrigW() * 2;
        int h = metadata.getOrigH() * 2;
        Mat image = new Mat();
        resize(convertedMat, image, new Size(w, h));
        for (DetectedObject obj : objs) {
          double[] xy1 = obj.getTopLeftXY();
          double[] xy2 = obj.getBottomRightXY();
          String label = labels.get(obj.getPredictedClass());
          int x1 = (int) Math.round(w * xy1[0] / gridWidth);
          int y1 = (int) Math.round(h * xy1[1] / gridHeight);
          int x2 = (int) Math.round(w * xy2[0] / gridWidth);
          int y2 = (int) Math.round(h * xy2[1] / gridHeight);
          rectangle(image, new Point(x1, y1), new Point(x2, y2), Scalar.RED);
          putText(image, label, new Point(x1 + 2, y2 - 2), FONT_HERSHEY_DUPLEX, 1, Scalar.GREEN);
        }
        frame.setTitle(new File(metadata.getURI()).getName() + " - HouseNumberDetection");
        frame.setCanvasSize(w, h);
        frame.showImage(converter.convert(image));
        frame.waitKey();
      }
      frame.dispose();
    }
  }
}
