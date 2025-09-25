package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;

@TeleOp
public class Physics extends LinearOpMode {

    final double lineLength = 17;
    double angle = 50;
    double v0 = 100;

    double distance = 15; //diagonal length of the field: ~16.9705627485 ft
    final double height = 3.233333; //38.80in

    double v0x = v0 * Math.cos(Math.toRadians(angle));
    double v0y = v0 * Math.sin(Math.toRadians(angle));

    public void runOpMode()
    {
        waitForStart();

        while (opModeIsActive())
        {
            if(gamepad1.dpad_up)
            {
                newAngle(angle+1);
                v0 = 100;
            }
            if(gamepad1.dpad_down)
            {
                newAngle(angle-1);
                v0 = 100;
            }
            newVel(findGoodVel(angle, distance));

            telemetry.addData("velocity", v0);
            telemetry.addData("angle", angle);
            telemetry.update();
            if (isStopRequested()) return;
        }
    }

    public double findGoodVel(double inAngle, double inDistance)
    {
        double min = Double.MAX_VALUE;
        double vel = v0;
        double velX = vel * Math.cos(Math.toRadians(angle));
        double velY = vel * Math.sin(Math.toRadians(angle));

        int count = 0;
        while(min > .5 || min <= 0) {
            if(min == Double.MAX_VALUE) newVel(1000);
            else if(min <= 0) newVel(vel + 0.01);
            else if(min > .5) newVel(vel - 0.01);
            v0x = vel * Math.cos(Math.toRadians(inAngle));
            v0y = vel * Math.sin(Math.toRadians(inAngle));
            min = getSmallestYDistance(getPath());
            count++;
            if(count > 1000) return -100;
        }
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
    private void newVel(double vel)
    {
        if(vel <= 0) v0 = 0.000001;
        else if(vel >= 100000) v0 = 1000000;
        else v0 = vel;
    }

    public List<Point> getPath()
    {
        List<Point> points = new ArrayList<Point>();

        double x = 0;
        double y = 0;

        for(float t = 0; t < 3; t += 0.02)
        {
            x = v0x * t;
            y = (v0y * t) - (4.9 * Math.pow(t, 2));

            points.add(new Point(x,y));
        }
        return points;
    }

    public double getSmallestYDistance(List<Point> points)
    {
        boolean above = false;
        int start;
        int end;
        ArrayList<Point> nearWall = new ArrayList<Point>();

        for(int i = 0; i < points.size(); i++)
        {
            Point p = points.get(i);
            if((-distance + p.x) > -0.5 && !above)
            {
                above = true;
                nearWall.add(p);
            }
            else if(above && (-distance + p.x) > 0.5)
            {
                nearWall.add(p);
                break;
            }
            if(above) nearWall.add(p);
        }
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
