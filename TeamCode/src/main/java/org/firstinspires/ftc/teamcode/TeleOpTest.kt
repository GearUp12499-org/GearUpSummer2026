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
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.HOOD_50
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.Locks
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.OUTTAKE_POWER
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOTER_STOP_DOWN
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOTER_STOP_UP
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOT_MID_RANGE
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.hoodAndSpeed
import org.firstinspires.ftc.teamcode.systems.TurretImpl
import org.firstinspires.ftc.teamcode.taskshark.Scheduler
import org.firstinspires.ftc.teamcode.taskshark.Task
import org.firstinspires.ftc.teamcode.taskshark.prefabs.Compose
import org.firstinspires.ftc.teamcode.taskshark.prefabs.Group
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

    private var activeTrack: TurretTrack.TrackTask? = null


    private fun startTrackingFull() {
        activeTrack = turretTrack.TrackTask()
        scheduler.add(activeTrack!!)
        TurretImpl.P = ACTIVE_TRACK_P
        TurretImpl.I = ACTIVE_TRACK_I
        TurretImpl.D = ACTIVE_TRACK_D

        val shooterTask = Compose{val hoodSpeed =
            activeTrack!!.distance?.let{ hoodAndSpeed(it) }
            shooter.setTarget(hoodSpeed?.second ?: SHOOT_MID_RANGE)
            hw.hood.position = (hoodSpeed?.first ?: HOOD_50)
        }
        scheduler.add(shooterTask)


        Log.i("shooterSpeed", hw.shoot1Vel.toString())
    }
    var needToStopIntake = false


    private fun shoot(): Group{
        val shoot = Group(
            OneShot{shooter.pushThreshold = 0},
            shooter.awaitTarget(
                minimumDuration = 0.0,
                maximumDuration = 0.75
            ),
            Combo.shoot(hw),
            OneShot{shooter.pushThreshold = shooter.defaultPushThreshold},
            Combo.shootAfter(hw)
        )
        if(needToStopIntake){
            return Group(
                Combo.intakeAfter(hw),
                shoot
            )
        }
        else{
            return shoot
        }

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

        hw.turretEncoder.reset()

        var was1X = false
        var was1RB = false
        var was1LB = false

        waitForStart()

        startTrackingFull()
        while(opModeIsActive()){
            scheduler.tick()

            val gp1X = gamepad1.x
            val gp1Rb = gamepad1.right_bumper
            val gp1Lb = gamepad1.left_bumper

            if(gp1Rb && !was1RB) {
                needToStopIntake = true
                was1RB = true
                scheduler.add(Combo.intake(hw)).require(Locks.INTAKE)
                    .then(Combo.intakeAfter(hw))
                    .then(OneShot{was1RB = false
                    needToStopIntake = false})
            }


            if(gp1X && !was1X){
                was1X = true
                scheduler.add(shoot()).require(Locks.INTAKE)
                    .then(OneShot{was1X = false})
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

