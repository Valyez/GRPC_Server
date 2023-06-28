package org.example;

import com.example.grpc.ImageRequest;
import com.example.grpc.ImageResponse;
import com.example.grpc.ImageServiceGrpc;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.util.Date;
import java.util.Random;

public class ImageServiceImpl extends ImageServiceGrpc.ImageServiceImplBase {

    static {
        OpenCV.loadLocally();
    }

    @Override
    public void play(ImageRequest request,
                     StreamObserver<ImageResponse> responseObserver) {
        Random random =new Random();
        int camId = request.getCamId();
        VideoCapture cap = new VideoCapture(camId);

        Mat frame = new Mat();
        MatOfByte buf = new MatOfByte();



        while (cap.grab()) {
            Date date = new Date();
            cap.read(frame);
            Imgcodecs.imencode(".png", frame, buf);
            ImageResponse response =
                    ImageResponse.newBuilder()
                            .setFrames(ByteString.copyFrom(buf.toArray()))
                            .setDate(date.toString())
                            .setTemperature(random.nextInt(10))
                            .build();

            responseObserver.onNext(response);

        }

        cap.release();
        responseObserver.onCompleted();
    }
}

