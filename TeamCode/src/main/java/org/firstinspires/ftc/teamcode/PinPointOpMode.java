package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.hardware.HardwareMapper;



@TeleOp
public class PinPointOpMode extends LinearOpMode {
    Hardware hardware;
    private void driveToPos(double targetX, double targetY){
        double KP = 0.02;

        while (true){
            hardware.pinpoint.update();
            Pose2D currentPos = hardware.pinpoint.getPosition();
            double X = currentPos.getX(DistanceUnit.INCH);
            double ErrorX = targetX-X;
            double Y = currentPos.getY(DistanceUnit.INCH);
            double ErrorY = Y-targetY;
            if (Math.sqrt((Math.abs(ErrorX) * Math.abs(ErrorX)) + (Math.abs(ErrorY) * Math.abs(ErrorY))) < 4 && Math.abs(hardware.pinpoint.getVelX(DistanceUnit.INCH)) < 4 && Math.abs(hardware.pinpoint.getVelY(DistanceUnit.INCH)) < 4 ){
                break;
            }
            double motorPower = KP*ErrorX;
            double motorPower2 = KP*ErrorY;
            double denominator = Math.abs(motorPower2+motorPower);
            if (motorPower + motorPower2 > 1){
                motorPower = motorPower/denominator;
                motorPower2 = motorPower2/denominator;

        }
            else if (motorPower + motorPower2 < -1) {
                motorPower = motorPower/denominator;
                motorPower2 = motorPower2/denominator;
            }

            hardware.frontLeft.setPower((motorPower + motorPower2));
            hardware.backLeft.setPower((motorPower - motorPower2));
            hardware.frontRight.setPower((motorPower - motorPower2));
            hardware.backRight.setPower((motorPower + motorPower2));
            Log.i("VelX",String.valueOf(hardware.pinpoint.getVelX(DistanceUnit.INCH)));
            Log.i("VelY",String.valueOf(hardware.pinpoint.getVelY(DistanceUnit.INCH)));
            Log.i("ErrorX",String.valueOf(ErrorX));
            Log.i("ErrorY",String.valueOf(ErrorY));
            Log.i("motorPower",String.valueOf(motorPower));
            Log.i("motorPower2",String.valueOf(motorPower2));
            Log.i("PinpointX",String.valueOf(X));
            Log.i("PinpointY",String.valueOf(Y));



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
                driveToPos(0,24);
            }

        }
    }
}
//positive Y target is to the left of the robot