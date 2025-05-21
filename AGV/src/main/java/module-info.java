module AGV {
    requires spring.web;
    requires CommonAGV;
    provides dk.sdu.sm4.common.agv.AGVClient with
            dk.sdu.sm4.agvlogic.AGVClientImpl;

    exports dk.sdu.sm4.agvlogic;
}