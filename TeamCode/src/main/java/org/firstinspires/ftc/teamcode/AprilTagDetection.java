package org.firstinspires.ftc.teamcode;

import static java.util.Collections.min;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class AprilTagDetection {

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    public static double middleOfScreenX = 320;
    public static double middleOfScreenY = 240;
    public static final double focalLengthMM = 2.8; //600.075; //2.8mm focal length approximately: Logitech c922 pro

    /**
     * The variable to store our instance of the AprilTag processor.
     */
    private AprilTagProcessor aprilTag;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    double toMM(double in)
    {
        return in*25.4;
    }

    public AprilTagDetection(HardwareMap hardwareMap, Telemetry telemetry)
    {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }
    public void initAprilTag() {

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()

                // The following default settings are available to un-comment and edit as needed.
                //.setDrawAxes(false)
                //.setDrawCubeProjection(false)
                //.setDrawTagOutline(true)
                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                //.setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)

                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
                // ... these parameters are fx, fy, cx, cy.

                .build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        //aprilTag.setDecimation(3);

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        //builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(aprilTag);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Disable or re-enable the aprilTag processor at any time.
        //visionPortal.setProcessorEnabled(aprilTag, true);

    }   // end method initAprilTag()


    /**
     * Add telemetry about AprilTag detections.
     */
    ArrayList<Boolean> colors = new ArrayList<>();

    public Point telemetryAprilTag() {

        List<org.firstinspires.ftc.vision.apriltag.AprilTagDetection> currentDetections = aprilTag.getDetections();
        //telemetry.addData("# AprilTags Detected", currentDetections.size());
        //telemetry.addData("color list ", colors.toString());
        Point cent = new Point();

        // Step through the list of detections and display info for each one.
        for (org.firstinspires.ftc.vision.apriltag.AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {

                //telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, colors.toString()));
                //telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                //telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                //telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                if(detection.id == 21) colors = new ArrayList<Boolean>(){{add(true); add(false); add(false);}};
                if(detection.id == 22) colors = new ArrayList<Boolean>(){{add(false); add(true); add(false);}};
                if(detection.id == 23) colors = new ArrayList<Boolean>(){{add(false); add(false); add(true);}};
                //telemetry.addData("Pose x", detection.center);
                cent = detection.center;
                Point[] corners = detection.corners;

                double width = Math.abs(Math.min(corners[0].x, corners[3].x) - Math.max(corners[1].x, corners[2].x));

                telemetry.addData("width", width);
                // distance = (realWorldWidth * focalLength) / perceived width
                // 1524mm = (4in * focalLength) /
                double distance = 108000/width;//need change according to data/
                telemetry.addData("distance?", distance/25.4);
                //telemetry.addData()
                //telemetry.addData("distance in MM?", distance * 31.75);
                //telemetry.addData("distance in in?", (distance * 31.75)/25.4 * (5/3));
                ///WHAT ARE ARBITRARY UNITSSS KEJRGJH WKHGIOEGHJ OERUIHKJSHRT IUSRHPUIOY5
                //telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                //telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }   // end for() loop

        // Add "key" information to telemetry
        //telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        //telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        //telemetry.addLine("RBE = Range, Bearing & Elevation");
        return cent;

    }   // end method telemetryAprilTag()
}
