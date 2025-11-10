package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class ShootTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor rotateMotor = hardwareMap.dcMotor.get("rotateMotor");
        while(opModeIsActive())
        {
            frontLeftMotor.setPower(gamepad1.left_stick_y);

            if(gamepad1.a) frontLeftMotor.setPower(1);

            rotateMotor.setPower(gamepad1.right_stick_y * 0.5);
        }
    }
}
