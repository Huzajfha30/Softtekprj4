package dk.sdu.sm4.enums;

public enum WarehouseState {
    IDLE(0),
    EXECUTING(1),
    ERROR(2);

    private final int code;

    WarehouseState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static WarehouseState fromCode(int code) {
        for (WarehouseState state : values()) {
            if (state.code == code) return state;
        }
        return null;
    }
}
