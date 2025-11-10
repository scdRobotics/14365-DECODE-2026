package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase.getCurrentGameTagLibrary;

import android.util.Size;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

@TeleOp
public class Test extends LinearOpMode
{
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    public static double middleOfScreenX = 320;
    public static double middleOfScreenY = 240;

    public static double angle = 45;

    /**
     * The variable to store our instance of the AprilTag processor.
     */

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() throws InterruptedException
    {
        getCurrentGameTagLibrary();
        Point center = new Point();
        ArrayList<Integer> colorList;
        AprilTagDetection aprilTagDetection = new AprilTagDetection(hardwareMap, telemetry);
        //Physics physics = new Physics(hardwareMap, telemetry);
        Intake intake = new Intake(hardwareMap, telemetry);



        waitForStart();
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        aprilTagDetection.initAprilTag();

        if (isStopRequested()) return;

        while (opModeIsActive())
        {
            intake.update();
            center = aprilTagDetection.telemetryAprilTag();
            //if(aprilTagDetection.colors.size() != 0 && colorList.size() == 0);
            colorList = aprilTagDetection.colors;
            if(colorList.size() != 0) intake.init(colorList);
            telemetry.addData("Color List", colorList);
            telemetry.addData("Center", center);

            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            double frontLeftPower = (y + x + rx);
            double backLeftPower = (y - x + rx);
            double frontRightPower = (y - x - rx);
            double backRightPower = (y + x - rx);

            boolean lastFrameLeft = false;
            boolean lastFrameRight = false;
            boolean lastFrameRightTrigger = false;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            if(center.x > middleOfScreenX - 5) telemetry.addLine("move right");
            if(center.x < middleOfScreenX + 5) telemetry.addLine("move left");

            if(center.y > middleOfScreenY - 5) telemetry.addLine("move down");
            if(center.y < middleOfScreenY + 5) telemetry.addLine("move up");


            if(gamepad1.dpad_right && !lastFrameRight)
            {
                intake.rotateCounterClockwise();
            }
            if(gamepad1.dpad_left && !lastFrameLeft)
            {
                intake.rotateClockwise();
            }
            if(gamepad1.right_trigger != 0 && !lastFrameRightTrigger)
            {
                //intake.orderedShoot(physics.findGoodVel(angle, aprilTagDetection.getDistance()));
            }


            lastFrameRight = gamepad1.dpad_right;
            lastFrameLeft = gamepad1.dpad_left;
            lastFrameRightTrigger = gamepad1.right_trigger != 0;
            
            telemetry.update();
        }
    }
}