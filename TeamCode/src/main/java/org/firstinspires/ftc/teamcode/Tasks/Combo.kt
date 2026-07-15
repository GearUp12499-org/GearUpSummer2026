package org.firstinspires.ftc.teamcode.Tasks

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.drivers.GoBildaPrismDriver.Artboard
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.BOTTOM_STOP_STOWED
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.FLIPPER_DOWN
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.FLIPPER_UP
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.INTAKE_POWER
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.OUTTAKE_POWER
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOTER_STOP_DOWN
import org.firstinspires.ftc.teamcode.hardware.HardwareMap.SHOOTER_STOP_UP
import org.firstinspires.ftc.teamcode.taskshark.Task
import org.firstinspires.ftc.teamcode.taskshark.prefabs.Group
import org.firstinspires.ftc.teamcode.taskshark.prefabs.OneShot
import org.firstinspires.ftc.teamcode.taskshark.prefabs.Wait
import org.firstinspires.ftc.teamcode.taskshark.prefabs.WaitUntil
import org.firstinspires.ftc.teamcode.taskshark.prefabs.WaitUntilContinuous

object Combo {
    fun intake(hw: HardwareMap, power: Double = INTAKE_POWER, timeout: Double = 0.5): Group {
        return Group(
            OneShot {
                hw.setIntakePower(0.0)
                hw.bottomBallStop.position = BOTTOM_STOP_STOWED
                hw.flipper.position = FLIPPER_DOWN
                hw.shooterBallStop.position = SHOOTER_STOP_DOWN
            },
            Wait.ms(250),
            OneShot { hw.setIntakePower(power) },
            WaitUntil {
                hw.colorTopLeft.getDistance(DistanceUnit.MM) < 95.0
                        || hw.colorTopRight.getDistance(DistanceUnit.MM) < 95.0
            },
            WaitUntilContinuous(timeout) {
                hw.frontRamp.state && hw.middleRamp.state
            },
            OneShot { hw.setIntakePower(0.0) }
        )
    }

    fun intakeAfter(hw: HardwareMap): Group {
        return Group(
            OneShot { hw.setIntakePower(0.0) },
            Wait.s(0.05),
            OneShot { hw.shooterBallStop.position = SHOOTER_STOP_UP },
            Wait.s(0.15)
        )
    }

    fun shoot(hw: HardwareMap, flipperWait: Double = 0.15, intakePower: Double = 1.0): Group {
        return Group(
            OneShot {
                hw.setIntakePower(intakePower)
                hw.bottomBallStop.position = BOTTOM_STOP_STOWED
                hw.shooterBallStop.position = SHOOTER_STOP_UP
            },
            WaitUntilContinuous(flipperWait, max = 1.0) {
                !hw.frontRamp.state && (hw.colorBottomLeft.getDistance(DistanceUnit.MM) < 110.0
                        || hw.colorBottomRight.getDistance(DistanceUnit.MM) < 110.0)
            },
            OneShot{hw.flipper.position = FLIPPER_UP},
            Wait.ms(400)
            )
    }

    fun shootAfter(hw: HardwareMap): Group{
        return Group(
            OneShot{
                hw.flipper.position = FLIPPER_DOWN
                hw.setIntakePower(OUTTAKE_POWER)
            },
            Wait.ms(500),
            OneShot{ hw.setIntakePower(0.0) }
        )
    }
}
