package org.firstinspires.ftc.teamcode.hardware;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp
public class Limelight extends LinearOpMode{
    double conversion = 39.37;
    private Limelight3A limelight;
    @Override
            public void runOpMode() throws InterruptedException {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(2);
        limelight.setPollRateHz(100);
        limelight.start();
        int counter = 0;
        int counter2 = 0;
        waitForStart();
        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if (result != null) {
                if (result.isValid()) {
                    Pose3D botpose = result.getBotpose();
                    if(botpose != null){
                        counter2 += 1;
                        telemetry.addData("tx", result.getTx());
                        telemetry.addData("ty", result.getTy());
                        telemetry.addData("X", botpose.getPosition().x*conversion);
                        telemetry.addData("Y", botpose.getPosition().y*conversion);
                        telemetry.addData("Z", botpose.getPosition().z*conversion);
                        telemetry.addData("Yaw", botpose.getOrientation().getYaw());
                        telemetry.addData("Pitch", botpose.getOrientation().getPitch());
                        telemetry.addData("Roll", botpose.getOrientation().getRoll());
                    }else{
                        telemetry.addLine("Null");
                    }
                }
            }
            counter += 1;
            telemetry.addData("Counter",counter);
            telemetry.addData("Counter2",counter2);
            telemetry.update();

        }
    }}