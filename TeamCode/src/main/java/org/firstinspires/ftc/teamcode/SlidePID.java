package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp
public class SlidePID extends LinearOpMode {
    SummerBotHardware hardware;
    private void lift(double target_height){
        double KP = 0.3;
        double KD = 0;
        double KI = 0;
        double maxHeight = 830;
        ElapsedTime held_at_target = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

        while(true){
            double currentPosition = hardware.linearSlide.getCurrentPosition();
            double Error = currentPosition - target_height;

            if(Math.abs(Error) > 10){
                held_at_target.reset();
            }
            if(held_at_target.time() > 5){
                Log.i("Break", "break");
                break;
            }
            double motorPower = KP * -Error;
            if(Math.abs(motorPower) > 1){
                motorPower = motorPower/Math.abs(motorPower);
            }
            if(currentPosition >= maxHeight){
                motorPower = -Math.abs(motorPower);
            }
            hardware.linearSlide.setPower(motorPower);
            Log.i("Error",String.valueOf(Error));
            Log.i("motorPower",String.valueOf(motorPower));
            Log.i("Current Position",String.valueOf(currentPosition));
        }
        hardware.linearSlide.setPower(0);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        hardware = new SummerBotHardware(hardwareMap);
        hardware.linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("Current Position",hardware.linearSlide.getCurrentPosition());
            telemetry.update();

            if(gamepad1.dpad_down){
                lift(500);
            }
            if(gamepad1.dpad_up){
                hardware.linearSlide.setPower(1.0);
            }
        }
    }
}
