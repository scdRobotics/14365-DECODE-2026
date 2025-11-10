package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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

    private NormalizedColorSensor colorSensor;

    public Intake(HardwareMap hardwareMap, Telemetry telemetry)
    {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        order.add(0);
        order.add(0);
        order.add(0);

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "internalColorSensor");

        balls.add(0);
        balls.add(0);
        balls.add(0);
    }
    public void init(ArrayList<Integer> goodOrder)
    {
        order.set(0, goodOrder.get(0));
        order.set(1, goodOrder.get(1));
        order.set(2, goodOrder.get(2));
    }

    @Override
    public void runOpMode()
    {
        /*
        order.add(2);
        order.add(1);
        order.add(1);

        NormalizedColorSensor colorSensor = hardwareMap.get(NormalizedColorSensor.class, "internalColorSensor");
        waitForStart();
        if(isStopRequested()) return;

        boolean lastFrameLeft = false;
        boolean lastFrameRight = false;
        boolean lastFrameRightTrigger = false;

        balls.add(0);
        balls.add(0);
        balls.add(0);
        */

        while(opModeIsActive())
        {
            red = colorSensor.getNormalizedColors().red;
            blue = colorSensor.getNormalizedColors().blue;
            green = colorSensor.getNormalizedColors().green;

            if(green > red + .001 && green > blue + .001 && green > 0.002)
            {
                logBalls();
                balls.set(0, 2);
            }
            if(blue > red + 0.001 && blue > green + 0.001 && blue > 0.002)
            {
                logBalls();
                balls.set(0,1);
            }

            /*if(gamepad1.dpad_right && !lastFrameRight)
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
                orderedShoot(0);
                logBalls();
                telemetry.update();
            }

            telemetry.update();

            lastFrameRight = gamepad1.dpad_right;
            lastFrameLeft = gamepad1.dpad_left;
            lastFrameRightTrigger = gamepad1.right_trigger != 0;*/
        }

    }
    public void update()
    {
        red = colorSensor.getNormalizedColors().red;
        blue = colorSensor.getNormalizedColors().blue;
        green = colorSensor.getNormalizedColors().green;

        if(green > red + .001 && green > blue + .001 && green > 0.002)
        {
            balls.set(0, 2);
        }
        if(blue > red + 0.001 && blue > green + 0.001 && blue > 0.002)
        {
            balls.set(0,1);
        }
        if(gamepad1.dpad_left) incrementOrderLocationDown();
        if(gamepad1.dpad_right) incrementOrderLocationUp();
        logBalls();
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

    public void incrementOrderLocationUp()
    {
        orderLocation++;
        orderLocation %= 3;
    }
    public void incrementOrderLocationDown()
    {
        orderLocation--;
        orderLocation = (orderLocation < 0 ? 2: orderLocation);
    }

    public void orderedShoot(double vel)
    {
        if(vel == -100)
        {
            telemetry.addLine("calculations failed :(");
            return;
        }
        int purple = purpleCollected();
        int green = greenCollected();

        while(!isEmpty())
        {
            int colorToShoot = order.get(orderLocation);

            if(balls.get(shootingPosition) == colorToShoot)
            {
                shoot(shootingPosition, vel);
            }
            else if(balls.get((shootingPosition+1)%balls.size()) == colorToShoot)
            {
                rotateClockwise();
                shoot(shootingPosition, vel);
            }
            else if(balls.get(shootingPosition-1 < 0 ? 2: shootingPosition-1) == colorToShoot)
            {
                rotateCounterClockwise();
                shoot(shootingPosition, vel);
            }
            else return;

            incrementOrderLocationUp();
        }
    }

    private void shoot(int shootingPosition, double vel)
    {
        telemetry.addData("SHOOOOOOOOOOOOOOOOOOOOOOOT", balls.get(shootingPosition));
        telemetry.addData("Velocity of shot: ", vel);
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
            if(order.get(i) == 1) telemetry.addLine("purple" + (i == orderLocation ? " <" : ""));
            else if(order.get(i) == 2) telemetry.addLine("green" + (i == orderLocation ? " <" : ""));
            else telemetry.addLine("nothing");
        }
    }
}