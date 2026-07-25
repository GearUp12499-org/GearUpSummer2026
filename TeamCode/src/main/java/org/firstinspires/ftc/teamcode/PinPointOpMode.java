package org.firstinspires.ftc.teamcode;

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
        double KP = 3;

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
            if (motorPower + motorPower2 > 1){
                motorPower = motorPower/(motorPower2 + motorPower);
                motorPower2 = motorPower2/(motorPower2 + motorPower);

        }
            else if (motorPower + motorPower2 < -1) {
                motorPower = -1*(motorPower/(motorPower2 + motorPower));
                motorPower2 = -1*(motorPower2/(motorPower2 + motorPower));
            }

            hardware.frontLeft.setPower((motorPower + motorPower2));
            hardware.backLeft.setPower((motorPower - motorPower2));
            hardware.frontRight.setPower((motorPower - motorPower2));
            hardware.backRight.setPower((motorPower + motorPower2));
            telemetry.addData("VelX",hardware.pinpoint.getVelX(DistanceUnit.INCH));
            telemetry.addData("VelY",hardware.pinpoint.getVelY(DistanceUnit.INCH));
            telemetry.addData("ErrorX",ErrorX);
            telemetry.addData("ErrorY",ErrorY);
            telemetry.update();



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
                driveToPos(24,24);
            }

        }
    }
}
//positive Y target is to the left of the robot