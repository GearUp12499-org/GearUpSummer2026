package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;


@TeleOp
public class PinPointOpMode extends LinearOpMode {
    Hardware hardware;
    private void driveToPos(double targetX, double targetY, double targetAngle){
        double KP = 0.45;
        double RP = 0.8;
        double KD = 0.05;
        double RD = 0.003;
        double FKI = 0; //0.065
        double SKI = 0; //0.095
        double RI = 0;//.65;
        double FISUM = 0;
        double SISUM = 0;
        double RISUM = 0;
        double PrevFError = 0;
        double PrevSError = 0;
        double PrevAngError = 0;
        double PrevTime = System.nanoTime()/1e9;


        while (true){
            hardware.pinpoint.update();
            Pose2D currentPos = hardware.pinpoint.getPosition();
            double X = currentPos.getX(DistanceUnit.INCH);
            double ErrorX = targetX - X;
            double Y = currentPos.getY(DistanceUnit.INCH);
            double ErrorY = targetY - Y;
            double Angle = currentPos.getHeading(AngleUnit.RADIANS);
            double ErrorAngle = targetAngle - Angle;
            ErrorAngle %= 2 * Math.PI;
            if(ErrorAngle > Math.PI){
                ErrorAngle -= 2 * Math.PI;
            } else if (ErrorAngle <= -Math.PI) {
                ErrorAngle += 2 * Math.PI;

            }
            double f = ErrorX * Math.cos(currentPos.getHeading(AngleUnit.RADIANS)) + ErrorY * Math.sin(currentPos.getHeading(AngleUnit.RADIANS));
            double s = -ErrorY * Math.cos(currentPos.getHeading(AngleUnit.RADIANS)) + ErrorX * Math.sin(currentPos.getHeading(AngleUnit.RADIANS));
            //if (Math.sqrt((Math.abs(ErrorX) * Math.abs(ErrorX)) + (Math.abs(ErrorY) * Math.abs(ErrorY))) < 4 && Math.abs(ErrorAngle) < 4 && Math.abs(hardware.pinpoint.getVelX(DistanceUnit.INCH)) < 4 && Math.abs(hardware.pinpoint.getVelY(DistanceUnit.INCH)) < 4 ){
            if (Math.abs(ErrorX) < 0.5 && Math.abs(ErrorY) < 0.5 && Math.abs(ErrorAngle) < 0.08 && Math.abs(hardware.pinpoint.getVelX(DistanceUnit.INCH)) < 4 && Math.abs(hardware.pinpoint.getVelY(DistanceUnit.INCH)) < 4 && Math.abs(hardware.pinpoint.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS)) < 0.1){
                Log.i("Break","");
                break;
            }
            double now = System.nanoTime()/1e9;
            double dt = now - PrevTime;
            if(Math.abs(f)<5){
                FISUM += f*dt;
                SISUM += f*dt;
                RISUM += f*dt;
            }
            double motorPower = KP * f + KD * (f - PrevFError) / dt + FKI * FISUM;
            double motorPower2 = KP * s + KD * (s - PrevSError) / dt + SKI * SISUM;
            double motorPower3 = RP * ErrorAngle + RD * (ErrorAngle - PrevAngError) / dt - RI * RISUM;
            double denominator = Math.abs(motorPower2+motorPower+motorPower3);
            if (motorPower + motorPower2 + motorPower3 > 1){
                motorPower = motorPower/denominator;
                motorPower2 = motorPower2/denominator;
                motorPower3 = motorPower3/denominator;

        }
            else if (motorPower + motorPower2 + motorPower3 < -1) {
                motorPower = motorPower/denominator;
                motorPower2 = motorPower2/denominator;
                motorPower3 = motorPower3/denominator;
            }

            hardware.frontLeft.setPower((motorPower + motorPower2 - motorPower3));
            hardware.backLeft.setPower((motorPower - motorPower2 - motorPower3));
            hardware.frontRight.setPower((motorPower - motorPower2 + motorPower3));
            hardware.backRight.setPower((motorPower + motorPower2 + motorPower3));
            Log.i("VelX",String.valueOf(hardware.pinpoint.getVelX(DistanceUnit.INCH)));
            Log.i("VelY",String.valueOf(hardware.pinpoint.getVelY(DistanceUnit.INCH)));
            Log.i("ErrorX",String.valueOf(ErrorX));
            Log.i("ErrorY",String.valueOf(ErrorY));
            Log.i("ErrorAngle",String.valueOf(ErrorAngle));
            Log.i("motorPower",String.valueOf(motorPower));
            Log.i("motorPower2",String.valueOf(motorPower2));
            Log.i("motorPower3",String.valueOf(motorPower3));
            Log.i("PinpointX",String.valueOf(X));
            Log.i("PinpointY",String.valueOf(Y));
            Log.i("PinpointAngle",String.valueOf(Angle));
            PrevTime = now;
            PrevFError = f;
            PrevSError = s;
            PrevAngError = ErrorAngle;



        }
        hardware.frontLeft.setPower(0);
        hardware.backLeft.setPower(0);
        hardware.frontRight.setPower(0);
        hardware.backRight.setPower(0);
    }
    @Override
    public void runOpMode() throws InterruptedException {
        hardware = new Hardware(hardwareMap);
        hardware.pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0,0, AngleUnit.RADIANS, 0));
        waitForStart();

        while(opModeIsActive()){
            hardware.pinpoint.update();

            Pose2D currentPos = hardware.pinpoint.getPosition();

            telemetry.addData("pinpointX",currentPos.getX(DistanceUnit.INCH));
            telemetry.addData("pinpointY",currentPos.getY(DistanceUnit.INCH));
            telemetry.addData("pinpointAngle",currentPos.getHeading(AngleUnit.DEGREES));
            telemetry.update();

            if (gamepad1.dpad_down){
                driveToPos(-24,24,0);
            }

        }
    }
}
//positive Y target is to the left of the robot