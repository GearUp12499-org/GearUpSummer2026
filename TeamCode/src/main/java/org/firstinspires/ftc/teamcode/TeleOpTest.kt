package org.firstinspires.ftc.teamcode

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import org.firstinspires.ftc.teamcode.Tasks.Combo
import org.firstinspires.ftc.teamcode.Tasks.PinpointTask
import org.firstinspires.ftc.teamcode.Tasks.ShooterImpl
import org.firstinspires.ftc.teamcode.Tasks.TurretTrack
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.ACTIVE_TRACK_D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.ACTIVE_TRACK_I
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.ACTIVE_TRACK_P
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.BOTTOM_STOP_STOWED
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.FLIPPER_DOWN
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.FLIPPER_UP
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.OUTTAKE_POWER
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOTER_STOP_DOWN
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOTER_STOP_UP
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOT_MID_RANGE
import org.firstinspires.ftc.teamcode.systems.TurretImpl
import org.firstinspires.ftc.teamcode.taskshark.Scheduler
import org.firstinspires.ftc.teamcode.taskshark.Task
import org.firstinspires.ftc.teamcode.taskshark.prefabs.OneShot
import org.firstinspires.ftc.teamcode.taskshark.prefabs.SentinelTask
import org.firstinspires.ftc.teamcode.taskshark.prefabs.Wait
import org.firstinspires.ftc.teamcode.taskshark.prefabs.WaitUntil
import org.firstinspires.ftc.teamcode.taskshark.prefabs.WaitUntilContinuous
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

@TeleOp
class TeleOpTest: LinearOpMode() {

    private val poseSet = PoseSet.RED
    private lateinit var scheduler: Scheduler
    private lateinit var hw: HardwareMap

    private lateinit var turret: TurretImpl

    private lateinit var turretTrack: TurretTrack

    private lateinit var shooter: ShooterImpl

    private var activeTrack: Task? = null


    private fun startTrackingFull() {
        activeTrack = scheduler.add(turretTrack.track())
        TurretImpl.P = ACTIVE_TRACK_P
        TurretImpl.I = ACTIVE_TRACK_I
        TurretImpl.D = ACTIVE_TRACK_D
    }

    override fun runOpMode() {


        scheduler = Scheduler()

        hw = HardwareMap(hardwareMap)

        turret = TurretImpl(hw)
        scheduler.add(turret)

        turretTrack = (TurretTrack(hw.limelight, turret, hw.pinpoint, poseSet, true))
        scheduler.add(turretTrack)

        shooter = ShooterImpl(hw)
        scheduler.add(shooter)

        hw.pinpoint.setPosition(Pose2D (DistanceUnit.INCH, 0.0, 0.0, AngleUnit.RADIANS, 0.0))
        scheduler.add(PinpointTask(hw.pinpoint))
        scheduler.add(DriveTask())

        var was1X = false
        var was1RB = false
        waitForStart()

        startTrackingFull()
        while(opModeIsActive()){
            scheduler.tick()

            val gp1X = gamepad1.x
            val gp1Rb = gamepad1.right_bumper

            if(gp1Rb && !was1RB){
                was1RB = true

                scheduler.add(
                    OneShot({
                        hw.setIntakePower(0.0)
                        hw.bottomBallStop.position = BOTTOM_STOP_STOWED
//                        if (hw.colorTopLeft.getDistance(DistanceUnit.MM) < 100.0) BOTTOM_BALL_STOP
//                        else BOTTOM_STOP_STOWED
                        hw.flipper.position = FLIPPER_DOWN
                        hw.shooterBallStop.position = SHOOTER_STOP_DOWN
                    })).require(HardwareMap.Locks.INTAKE)
                        .then(Wait.ms(250)).require(HardwareMap.Locks.INTAKE)
                        .then(OneShot {
                            hw.setIntakePower(1.0)
                        }).require(HardwareMap.Locks.INTAKE)
                        .then(WaitUntil {
                            hw.colorTopLeft.getDistance(DistanceUnit.MM) < 95.0
                                    || hw.colorTopRight.getDistance(DistanceUnit.MM) < 95.0
                        }).require(HardwareMap.Locks.INTAKE)
                        .then(WaitUntilContinuous(0.5) {
                            hw.frontRamp.state && hw.middleRamp.state
                        }).require(HardwareMap.Locks.INTAKE)
                        .then(OneShot{hw.setIntakePower(0.0)}).require(HardwareMap.Locks.INTAKE)
                    .then(OneShot{was1RB = false})
            }

            if(gp1X && !was1X){
                was1X = true
                scheduler.add(OneShot {
                            hw.setIntakePower(1.0)
                            hw.bottomBallStop.position = BOTTOM_STOP_STOWED
                            hw.shooterBallStop.position = SHOOTER_STOP_UP
//                        hw.prism.loadAnimationsFromArtboard(Artboard.ARTBOARD_4)
                            }).require(HardwareMap.Locks.INTAKE)
                            .then(WaitUntilContinuous(0.15, max = 1.0) {
                                !hw.frontRamp.state && (hw.colorBottomLeft.getDistance(DistanceUnit.MM) < 110.0
                                        || hw.colorBottomRight.getDistance(DistanceUnit.MM) < 110.0)
                            }).require(HardwareMap.Locks.INTAKE)
                                    .then(OneShot {
                                        hw.flipper.position = FLIPPER_UP
                                    }).require(HardwareMap.Locks.INTAKE)
                                    .then(Wait.ms(400))
                    .then(OneShot {
                    hw.flipper.position = FLIPPER_DOWN
                    hw.setIntakePower(OUTTAKE_POWER)
                }).require(HardwareMap.Locks.INTAKE)
                    .then(Wait.ms(500))
                    .then(OneShot {
                        hw.setIntakePower(0.0)
                    }).then(OneShot{was1X = false})
            }





            val pinpointPose = hw.pinpoint.position
            telemetry.addData("pinpoint X", pinpointPose.getX(DistanceUnit.INCH))
            telemetry.addData("pinpoint Y", pinpointPose.getY(DistanceUnit.INCH))
            telemetry.update()
        }
    }
    private inner class DriveTask: Task(){

        override fun onStart() {

        }

        override fun onTick(): Boolean {
            mecanumDispatcher()
            return false
        }

        override fun onFinish(completedNormally: Boolean) {
        }

        fun mecanumDispatcher() {
            val y = -gamepad1.left_stick_y.toDouble()
            val x = gamepad1.left_stick_x.toDouble()
            val rx = gamepad1.right_stick_x.toDouble()
            val pushValue = max(hypot(x, y), rx)
            mecanum(y, x, rx)
            //reset pinpoint heading
            if (gamepad1.backWasPressed()) {
                val pos = hw.pinpoint.position
                val new = Pose2D(
                    DistanceUnit.INCH,
                    pos.getX(DistanceUnit.INCH),
                    pos.getY(DistanceUnit.INCH),
                    AngleUnit.RADIANS,
                    0.0
                )
                hw.pinpoint.position = new
            }
        }

        fun mecanum(y: Double, x: Double, rx: Double) {
            val botHeading: Double = hw.pinpoint.getHeading(AngleUnit.RADIANS) + PI/2

            var rotX = x * cos(-botHeading) - y * sin(-botHeading)
            val rotY = x * sin(-botHeading) + y * cos(-botHeading)

            rotX *= 1.1 // Counteract imperfect strafing

            val denominator = max(abs(rotY) + abs(rotX) + abs(rx), 1.0)
            val frontLeftPower = (rotY + rotX + rx) / denominator
            val backLeftPower = (rotY - rotX + rx) / denominator
            val frontRightPower = (rotY - rotX - rx) / denominator
            val backRightPower = (rotY + rotX - rx) / denominator

            hw.frontLeft.power = frontLeftPower
            hw.backLeft.power = backLeftPower
            hw.frontRight.power = frontRightPower
            hw.backRight.power = backRightPower
        }

    }
}

