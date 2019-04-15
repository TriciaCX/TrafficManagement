package util;

public enum CarStateEnum {
    NoBlockPossibleToJump(1, "前方没有阻挡，经过其速度可以出路口"),
    NoBlockCannotJump(2, "前方没有阻挡，经过其速度不可以出路口"),
    Blocked1(3, "前方阻挡车辆为状态1的车"),
    Blocked2(4, "前方阻挡车辆为状态2的车"),
    NoBlockFive(5, "本来是1车，因为安排的路未更新，变成这一类车"),
    GarageWait(-1, "为发车");

    private int state;
    private String info;

    CarStateEnum(int state, String info) {
        this.state = state;
        this.info = info;
    }

    public static CarStateEnum stateOf(int index) {
        for (CarStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public String getInfo() {
        return info;
    }
}
