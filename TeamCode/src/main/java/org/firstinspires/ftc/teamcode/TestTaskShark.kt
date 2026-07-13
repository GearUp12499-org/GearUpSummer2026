package org.firstinspires.ftc.teamcode

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

import org.firstinspires.ftc.teamcode.taskshark.Scheduler
import org.firstinspires.ftc.teamcode.taskshark.prefabs.OneShot

@TeleOp
class TestTaskShark: LinearOpMode() {


    override fun runOpMode() {
        val sch = Scheduler()

        waitForStart()

        while (opModeIsActive()) {

            sch.tick()

        }

    }
}