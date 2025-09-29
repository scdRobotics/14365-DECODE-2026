package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;


@TeleOp(name="Touch Sensor", group="Sensor")
public class SensorTouch extends LinearOpMode
{
    TouchSensor touchSensor = null;

    String pressed = null;

    @Override public void runOpMode()
    {
        telemetry.addData("TouchSensor", "Initialized");
        touchSensor = hardwareMap.get(TouchSensor.class, "sensor_touch");

        waitForStart();
        readSensor();

    }


    private void readSensor()
    {

        while(opModeIsActive())
        {
            if (touchSensor.isPressed())
            {
                pressed = "Pressed";
            }
            else
            {
                pressed = "Not Pressed";
            }
            telemetry.addData("Touch Sensor Pressed", pressed);

            telemetry.update();
        }
    }
}
