package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import java.util.ArrayList;

@TeleOp
public class Intake extends LinearOpMode
{

    ArrayList<Integer> balls = new ArrayList<>();
    ArrayList<Integer> order = new ArrayList<>();

    int shootingPosition = 1;

    float red;
    float green;
    float blue;

    int orderLocation;


    @Override
    public void runOpMode()
    {
        order.add(2);
        order.add(1);
        order.add(1);

        NormalizedColorSensor colorSensor = hardwareMap.get(NormalizedColorSensor.class, "internalColorSensor");
        waitForStart();
        if(isStopRequested()) return;

        boolean lastFrameLeft = false;
        boolean lastFrameRight = false;
        boolean lastFrameRightTrigger = false;
        boolean lastFrameLeftTrigger = false;
        boolean lastFrameDown = false;

        balls.add(0);
        balls.add(0);
        balls.add(0);

        telemetry.setAutoClear(false);

        while(opModeIsActive())
        {
            red = colorSensor.getNormalizedColors().red;
            blue = colorSensor.getNormalizedColors().blue;
            green = colorSensor.getNormalizedColors().green;

            if(gamepad1.dpad_right && !lastFrameRight)
            {
                rotateCounterClockwise();
            }
            if(gamepad1.dpad_left && !lastFrameLeft)
            {
                rotateClockwise();
            }
            if(gamepad1.right_trigger != 0 && !lastFrameRightTrigger)
            {
                telemetry.clear();
                orderedShoot();
                logBalls();
                telemetry.update();
            }
            if(gamepad1.left_trigger != 0 && !lastFrameLeftTrigger)
            {
                telemetry.clear();
                shoot(shootingPosition);
                logBalls();
                telemetry.update();
            }
            if(gamepad1.dpad_down && !lastFrameDown)
            {
                incrementOrderLocation();
            }

            if(green > red + .001 && green > blue + .001 && green > 0.002)
            {
                telemetry.clear();
                logBalls();
                balls.set(0, 2);
            }
            if(blue > red + 0.001 && blue > green + 0.001 && blue > 0.002)
            {
                telemetry.clear();
                logBalls();
                balls.set(0,1);
            }
            telemetry.update();

            lastFrameRight = gamepad1.dpad_right;
            lastFrameLeft = gamepad1.dpad_left;
            lastFrameRightTrigger = gamepad1.right_trigger != 0;
            lastFrameLeftTrigger = gamepad1.left_trigger != 0;
            lastFrameDown = gamepad1.dpad_down;
        }

    }
    public boolean isEmpty()
    {
        return balls.get(0) == 0 && balls.get(1) == 0 && balls.get(2) == 0;
    }
    public int greenCollected()
    {
        int count = 0;

        for(int i = 0; i < balls.size(); i++)
        {
            if(balls.get(i) == 2) count++;
        }
        return count;
    }
    public int purpleCollected()
    {
        int count = 0;

        for(int i = 0; i < balls.size(); i++)
        {
            if(balls.get(i) == 1) count++;
        }
        return count;
    }

    public void incrementOrderLocation()
    {
        if(orderLocation == 2) orderLocation = 0;
        else orderLocation++;
        telemetry.clear();
        logBalls();
    }

    public void orderedShoot()
    {
        int purple = purpleCollected();
        int green = greenCollected();

        while(!isEmpty())
        {
            int colorToShoot = order.get(orderLocation);

            if(balls.get(shootingPosition) == colorToShoot)
            {
                shoot(shootingPosition);
            }
            else if(balls.get((shootingPosition+1)%balls.size()) == colorToShoot)
            {
                rotateClockwise();
                shoot(shootingPosition);
            }
            else if(balls.get(shootingPosition-1 < 0 ? 2: shootingPosition-1) == colorToShoot)
            {
                rotateCounterClockwise();
                shoot(shootingPosition);
            }
            else return;

            incrementOrderLocation();
        }
    }

    private void shoot(int shootingPosition)
    {
        telemetry.addData("SHOOOOOOOOOOOOOOOOOOOOOOOT", balls.get(shootingPosition));
        balls.set(shootingPosition, 0);
    }

    public void rotateCounterClockwise()
    {
        telemetry.addLine("turn counterclockwise");
        ArrayList<Integer> newBalls = new ArrayList<>();

        newBalls.add(0);
        newBalls.add(0);
        newBalls.add(0);

        for(int i = 0; i < balls.size(); i++)
        {
            newBalls.set( (i+1)%balls.size(),balls.get(i) );
        }
        balls = newBalls;
    }
    public void rotateClockwise()
    {
        telemetry.addLine("turn clockwise");
        ArrayList<Integer> newBalls = new ArrayList<>();

        newBalls.add(0);
        newBalls.add(0);
        newBalls.add(0);

        for(int i = 0; i < balls.size(); i++)
        {
            newBalls.set( (i-1 < 0 ? 2: i-1), balls.get(i));
        }
        balls = newBalls;
    }
    private void logBalls()
    {
        telemetry.addData("red", red);
        telemetry.addData("blue", blue);
        telemetry.addData("green", green);

        telemetry.addLine("\nhave balls:");
        for(int i : balls)
        {
            if(i == 0) telemetry.addLine("none");
            else if(i == 1) telemetry.addLine("purple");
            else telemetry.addLine("green");
        }
        telemetry.addLine("\ncorrect order:");
        for(int i = 0; i < order.size(); i++)
        {
            if(order.get(i) == 1) telemetry.addLine("purple");
            else telemetry.addLine("green");
            if(i == orderLocation) telemetry.addLine("/\\");
        }
    }

}