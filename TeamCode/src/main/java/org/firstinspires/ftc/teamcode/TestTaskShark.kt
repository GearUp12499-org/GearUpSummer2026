package org.firstinspires.ftc.teamcode

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import io.github.gearup12499.taskshark.FastScheduler
import io.github.gearup12499.taskshark.prefabs.OneShot

@TeleOp
class TestTaskShark: LinearOpMode() {


    override fun runOpMode() {
        val sch = FastScheduler()

        waitForStart()

        while (opModeIsActive()) {
            sch.add(OneShot { Log.i("test", "yay it works") })

            sch.tick()

        }

    }
}