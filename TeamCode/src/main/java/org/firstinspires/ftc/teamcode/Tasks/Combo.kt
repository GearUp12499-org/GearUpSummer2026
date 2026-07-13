package org.firstinspires.ftc.teamcode.Tasks

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.drivers.GoBildaPrismDriver.Artboard
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.BOTTOM_STOP_STOWED
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.FLIPPER_DOWN
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.INTAKE_POWER
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOTER_STOP_DOWN
import org.firstinspires.ftc.teamcode.taskshark.Task
import org.firstinspires.ftc.teamcode.taskshark.prefabs.OneShot
import org.firstinspires.ftc.teamcode.taskshark.prefabs.Wait
import org.firstinspires.ftc.teamcode.taskshark.prefabs.WaitUntil
import org.firstinspires.ftc.teamcode.taskshark.prefabs.WaitUntilContinuous

object Combo {
    fun intake(hw: HardwareMap, power: Double = INTAKE_POWER, timeout: Double = 0.5) =
        object : Task() {
            override fun onStart() {
                TODO("Not yet implemented")
            }

            override fun onTick(): Boolean {
                TODO("Not yet implemented")
            }

            override fun onFinish(completedNormally: Boolean) {
                TODO("Not yet implemented")
            }

            init {
                OneShot({
                    hw.setIntakePower(0.0)
                    hw.bottomBallStop.position = BOTTOM_STOP_STOWED
//                        if (hw.colorTopLeft.getDistance(DistanceUnit.MM) < 100.0) BOTTOM_BALL_STOP
//                        else BOTTOM_STOP_STOWED
                    hw.flipper.position = FLIPPER_DOWN
                    hw.shooterBallStop.position = SHOOTER_STOP_DOWN
                })
                    .then(Wait.ms(250))
                    .then(OneShot {
                        hw.setIntakePower(power)
                    })
                    .then(WaitUntil {
                        hw.colorTopLeft.getDistance(DistanceUnit.MM) < 95.0
                                || hw.colorTopRight.getDistance(DistanceUnit.MM) < 95.0
                    })
                    .then(WaitUntilContinuous(timeout) {
                        hw.frontRamp.state && hw.middleRamp.state
                    })
                    .then(OneShot{hw.setIntakePower(0.0)})
                this.require(HardwareMap.Locks.INTAKE)
            }
        }
}