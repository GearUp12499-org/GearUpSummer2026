package org.firstinspires.ftc.teamcode

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import kotlin.math.abs

@TeleOp

public class TestKotlinPID: LinearOpMode() {
    lateinit var hardware: Hardware
    fun driveToPose(targetX:Double,targetY:Double,targetAngle:Double){
        val KP = 0.5
        val RP = 0.5
        val KD = 0.07
        val RD = 0.01
        val FKI = 0.065
        val SKI = 0.095
        val RI = 0.65
        var FISUM = 0.0
        var SISUM = 0.0
        var RISUM = 0.0
        var PrevFError = 0.0
        var PrevSError = 0.0
        var PrevAngError = 0.0
        var PrevTime = System.nanoTime()/1e9

        while (true){
            hardware.pinpoint.update()
            val currentPos: Pose2D = hardware.pinpoint.getPosition()
            val X = currentPos.getX((DistanceUnit.INCH))
            val ErrorX = targetX - X
            val Y = currentPos.getY(DistanceUnit.INCH)
            val ErrorY = targetY - Y
            val Angle = currentPos.getHeading(AngleUnit.RADIANS)
            var ErrorAngle = targetAngle - Angle
            ErrorAngle %= 2 * Math.PI
            if(ErrorAngle > Math.PI){
                ErrorAngle -= 2 * Math.PI
            }  else if(ErrorAngle <= -Math.PI) {
                ErrorAngle += 2 * Math.PI
            }
            val f = ErrorX * Math.cos(currentPos.getHeading(AngleUnit.RADIANS)) + ErrorY * Math.sin(currentPos.getHeading(AngleUnit.RADIANS))
            val s = -ErrorY * Math.cos(currentPos.getHeading(AngleUnit.RADIANS)) + ErrorX * Math.sin(currentPos.getHeading(AngleUnit.RADIANS));

            if (abs(ErrorX) < 0.5 && abs(ErrorY) < 0.5 && abs(ErrorAngle) < 0.08 && abs(
                    hardware.pinpoint.getVelX(
                        DistanceUnit.INCH
                    )
                ) < 4 && abs(hardware.pinpoint.getVelY(DistanceUnit.INCH)) < 4 && abs(
                    hardware.pinpoint.getHeadingVelocity(
                        UnnormalizedAngleUnit.RADIANS
                    )
                ) < 0.1
            ){
                break
            }
            val now = System.nanoTime()/1e9
            val dt = now - PrevTime
            if(Math.abs(f)<5){
                FISUM += f*dt
            }
            if(Math.abs(s)<5){
                SISUM += s*dt
            }
            if(Math.abs(ErrorAngle)<0.5){
                RISUM += ErrorAngle*dt
            }
            var motorPower = KP * f + KD * (f - PrevFError) / (now - PrevTime) + FKI * FISUM
            var motorPower2 = KP * s + KD * (s - PrevSError) / (now - PrevTime) + SKI * SISUM
            var motorPower3 = RP * ErrorAngle + RD * (ErrorAngle - PrevAngError) / (now - PrevTime) + RI * RISUM
            val denominator = Math.abs(motorPower2 + motorPower + motorPower3)
            if (denominator > 1){
                motorPower3 = motorPower3/denominator
                motorPower2 = motorPower2/denominator
                motorPower = motorPower/denominator
            }
            hardware.frontLeft.setPower((motorPower + motorPower2 - motorPower3))
            hardware.backLeft.setPower((motorPower - motorPower2 - motorPower3))
            hardware.frontRight.setPower((motorPower - motorPower2 + motorPower3))
            hardware.backRight.setPower((motorPower + motorPower2 + motorPower3))
            Log.i("VelX", hardware.pinpoint.getVelX(DistanceUnit.INCH).toString())
            Log.i("VelY", hardware.pinpoint.getVelY(DistanceUnit.INCH).toString())
            Log.i("ErrorX", ErrorX.toString())
            Log.i("ErrorY", ErrorY.toString())
            Log.i("ErrorAngle", ErrorAngle.toString())
            Log.i("motorPower", motorPower.toString())
            Log.i("motorPower2", motorPower2.toString())
            Log.i("motorPower3", motorPower3.toString())
            Log.i("PinpointX", X.toString())
            Log.i("PinpointY", Y.toString())
            Log.i("PinpointAngle", Angle.toString())
            PrevTime = now
            PrevFError = f
            PrevSError = s
            PrevAngError = ErrorAngle
        }
        hardware.frontLeft.setPower(0.0)
        hardware.backLeft.setPower(0.0)
        hardware.frontRight.setPower(0.0)
        hardware.backRight.setPower(0.0)

    }
    override fun runOpMode() {
        hardware = Hardware(hardwareMap)
        hardware.pinpoint.setPosition(Pose2D(DistanceUnit.INCH,0.0,0.0, AngleUnit.RADIANS,0.0))
        waitForStart()

        while(opModeIsActive()){
            hardware.pinpoint.update()
            val currentPos: Pose2D = hardware.pinpoint.getPosition()
            telemetry.addData("pinpointX", currentPos.getX(DistanceUnit.INCH))
            telemetry.addData("pinpointY", currentPos.getY(DistanceUnit.INCH))
            telemetry.addData("pinpointAngle", currentPos.getHeading(AngleUnit.DEGREES))
            telemetry.update()

            if(gamepad1.dpad_down){
                driveToPose(-24.0,24.0,0.0)
            }

        }


    }
}
//positive Y target is to the left of the robot