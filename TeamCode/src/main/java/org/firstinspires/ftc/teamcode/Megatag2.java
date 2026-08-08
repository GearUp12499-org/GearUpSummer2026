package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import org.firstinspires.ftc.teamcode.hardware.HardwareMapper;

@TeleOp
public class Megatag2 extends LinearOpMode {
    double conversion = 39.37;
    Hardware hardware;

    @Override
    public void runOpMode() throws InterruptedException {
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(2);
        limelight.setPollRateHz(100);
        limelight.start();
        hardware = new Hardware(hardwareMap);
        hardware.pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.RADIANS, 0));
        waitForStart();
        while (opModeIsActive()) {
            hardware.pinpoint.update();
            Pose2D currentPos = hardware.pinpoint.getPosition();
            double heading = currentPos.getHeading(AngleUnit.DEGREES)+180;
            limelight.updateRobotOrientation(heading);
            LLResult result = limelight.getLatestResult();
            if (result != null) {
                if (result.isValid()) {
                    Pose3D botpose = result.getBotpose();
                    if(botpose != null){
//                        telemetry.addData("tx", result.getTx());
//                        telemetry.addData("ty", result.getTy());
//                        telemetry.addData("X", botpose.getPosition().x*conversion);
//                        telemetry.addData("Y", botpose.getPosition().y*conversion);
//                        telemetry.addData("Z", botpose.getPosition().z*conversion);
                        telemetry.addData("Mt1_Yaw", botpose.getOrientation().getYaw());
//                        telemetry.addData("Pitch", botpose.getOrientation().getPitch());
//                        telemetry.addData("Roll", botpose.getOrientation().getRoll());
                    }
                    Pose3D botpose_mt2 = result.getBotpose_MT2();
                    if (botpose != null) {
                        double X = botpose_mt2.getPosition().x;
                        double Y = botpose_mt2.getPosition().y;
                        double llHeading = botpose_mt2.getOrientation().getYaw(AngleUnit.DEGREES);
                        telemetry.addData("X", X*conversion);
                        telemetry.addData("Y", Y*conversion);
                        telemetry.addData("Pinpoint Heading",heading);
                        telemetry.addData("LL Heading",llHeading);

                    }
                }
            }
            telemetry.update();
        }
    }
}
