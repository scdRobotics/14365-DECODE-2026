package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name = "Sensor: Color", group = "Sensor")
public class ColorSensor extends LinearOpMode
{
    NormalizedColorSensor colorSensor;
    public static String ballColor;

    View relativeLayout;
    @Override public void runOpMode()
    {
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        try
        {
            runSensor();
        }
        finally {

        }
    }

    void runSensor()
    {
        waitForStart();
        //set the value that the raw value will be multiplied by
        float gain = 25;


        //hue, saturation, value
        final float[] hsvValues = new float[3];

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor_color");

        while (opModeIsActive())
        {
            colorSensor.setGain(gain);

            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            //update the values in the array
            Color.colorToHSV(colors.toColor(), hsvValues);

            if (hsvValues[0] > 200 && hsvValues[0] < 240)
            {
                ballColor = "Purple";
            }
            else if (hsvValues[0] > 155 && hsvValues[0] < 183)
            {
                ballColor = "Green";
            }
            else
            {
                ballColor = "NoBall";
            }

            //add the telemetry data relating to colors
            telemetry.addLine()
                    .addData("Red", "%.3f", colors.red)
                    .addData("Green", "%.3f", colors.green)
                    .addData("Blue", "%.3f", colors.blue);
            telemetry.addLine()
                    .addData("Hue", "%.3f", hsvValues[0])
                    .addData("Saturation", "%.3f", hsvValues[1])
                    .addData("Value", "%.3f", hsvValues[2]);
            telemetry.addData("Alpha", "%.3f", colors.alpha);
            telemetry.addData("BallColor", ballColor);


            telemetry.update();

        }
    }


}