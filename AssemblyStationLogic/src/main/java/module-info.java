module AssemblyStationLogic {
    requires CommonAssemblyStation;
    requires org.eclipse.paho.client.mqttv3;
    requires org.json;


    provides dk.sdu.sm4.commonassemblystation.IAssemblyStationService with
            dk.sdu.sm4.assemblystationlogic.AssemblyStationServiceImpl;

    exports dk.sdu.sm4.assemblystationlogic;

}