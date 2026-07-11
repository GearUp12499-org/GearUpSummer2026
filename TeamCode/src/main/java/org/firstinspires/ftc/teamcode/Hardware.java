package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drivers.GoBildaPinpoint2Driver;
import org.firstinspires.ftc.teamcode.hardware.HardwareMapper;
import org.firstinspires.ftc.teamcode.hardware.HardwareName;
import org.firstinspires.ftc.teamcode.hardware.Reversed;
import org.firstinspires.ftc.teamcode.hardware.ZeroPower;


public class Hardware extends HardwareMapper {

    @HardwareName("frontRight")
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx frontRight;

    @HardwareName("frontLeft")
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx frontLeft;

    @HardwareName("backRight")
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    @Reversed
    public DcMotorEx backRight;

    @HardwareName("backLeft")
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    @Reversed
    public DcMotorEx backLeft;

    @HardwareName("pinpoint")
    public GoBildaPinpoint2Driver pinpoint;

    public Hardware(HardwareMap map) {
        super(map);
//        pinpoint.setOffsets(-3.75,-6, DistanceUnit.INCH);
        pinpoint.setOffsets(-2.933,-5.02, DistanceUnit.INCH);
        pinpoint.setEncoderResolution(GoBildaPinpoint2Driver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpoint2Driver.EncoderDirection.REVERSED,GoBildaPinpoint2Driver.EncoderDirection.FORWARD);
    }
}
