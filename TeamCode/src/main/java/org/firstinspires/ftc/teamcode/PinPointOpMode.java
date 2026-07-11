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

        }
    }
}
