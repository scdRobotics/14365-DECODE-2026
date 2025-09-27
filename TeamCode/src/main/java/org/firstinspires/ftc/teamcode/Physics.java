package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

@TeleOp
public class Physics extends LinearOpMode {

    final double lineLength = 17; ///what is this meant to be?

    double angle = 50;
    double v0 = 100;

    double distance = 15; //diagonal length of the field: ~16.9705627485 ft
    final double height = 3.233333; //38.80in


    public void runOpMode()
    {
        waitForStart();

        v0 = findGoodVel(angle, distance);
        boolean justPressed = false;

        while (opModeIsActive())
        {
            if(gamepad1.dpad_up)
            {
                newAngle(angle + .001);
                justPressed = true;
                telemetry.addData("new angle", angle);
                telemetry.update();
            }
            else if(gamepad1.dpad_down) {
                newAngle(angle - .001);
                justPressed = true;
                telemetry.addData("new angle", angle);
                telemetry.update();
            }
            else if(justPressed){
                v0 = findGoodVel(angle, distance);
                justPressed = false;
            }

            if (isStopRequested()) return;
        }
    }

    public double findGoodVel(double inAngle, double inDistance)
    {
        double min = Double.MAX_VALUE;
        double vel = v0;

        int count = 0;
        while(min > .25 || min <= 0) {
            if(min == Double.MAX_VALUE) vel = restrictVel(100);
            else if(min <= 0) vel = restrictVel(vel + 0.1);
            else if(min > .25) vel = restrictVel(vel - 0.1);
            min = getSmallestYDistance(getPath(vel, inAngle), inDistance);
            count++;
            if(count > 10000) return -100;
        }
        telemetry.addData("velocity", vel);
        telemetry.addData("angle", angle);
        telemetry.addData("min", min);
        telemetry.update();
        return vel;
    }

    private void newAngle(double newAngle)
    {
        if(newAngle <= 0) angle = 0.0000001;
        else if(newAngle >= 90) angle = 89.9999999;
        else angle = newAngle;
    }
    private void newV0(double vel)
    {
        if(vel <= 0) v0 = 0.000001;
        else if(vel >= 100000) v0 = 1000000;
        else v0 = vel;
    }
    private double restrictVel(double vel)
    {
        if(vel <= 0) vel = 0.000001;
        else if(vel >= 100000) vel = 1000000;
        return vel;
    }

    public List<Point> getPath(double vel, double inAngle)
    {
        List<Point> points = new ArrayList<Point>();

        double x = 0;
        double y = 0;

        double velX = vel * Math.cos(Math.toRadians(inAngle));
        double velY = vel * Math.sin(Math.toRadians(inAngle));

        for(float t = 0; t < 3; t += 0.001)
        {
            x = velX * t;
            y = (velY * t) - (4.9 * Math.pow(t, 2));

            points.add(new Point(x,y));
        }
        return points;
    }

    public double getSmallestYDistance(List<Point> points, double inDistance)
    {
        boolean above = false;
        ArrayList<Point> nearWall = new ArrayList<Point>();

        if(points.get(points.size() - 1).x - inDistance <= 0){
            telemetry.addLine("doesn't reach wall");
            return Double.MAX_VALUE;
        }

        for(int i = 0; i < points.size(); i++)
        {
            Point p = points.get(i);
            if((-inDistance + p.x) > -0.25 && !above)
            {
                above = true;
                nearWall.add(p);
            }
            else if(above && (-inDistance + p.x) > 0.25)
            {
                nearWall.add(p);
                break;
            }
            if(above) nearWall.add(p);
        }
        telemetry.addData("near wall", nearWall);
        telemetry.update();

        double min = Double.MAX_VALUE;
        for(Point p : nearWall)
        {
            min = Math.min(p.y - height + 0.5, min);
        }
        return min;
    }
}

class Point
{
    public double x = 0;
    public double y = 0;
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString()
    {
        return "(" + (int)x + ", " + (int)y + ")";
    }
}
