package org.firstinspires.ftc.teamcode

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import io.github.gearup12499.taskshark.FastScheduler
import io.github.gearup12499.taskshark.prefabs.OneShot


class TestTaskShark: LinearOpMode() {


    override fun runOpMode() {
        val sch = FastScheduler()

        sch.add(OneShot{Log.i("test", "yay it works")})
    }
}