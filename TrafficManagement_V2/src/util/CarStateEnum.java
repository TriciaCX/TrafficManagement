package util;

public enum CarStateEnum {
    NoBlockPossibleToJump(1, "ǰ��û���赲���������ٶȿ��Գ�·��"),
    NoBlockCannotJump(2, "ǰ��û���赲���������ٶȲ����Գ�·��"),
    Blocked1(3, "ǰ���赲����Ϊ״̬1�ĳ�"),
    Blocked2(4, "ǰ���赲����Ϊ״̬2�ĳ�"),
    NoBlockFive(5, "������1������Ϊ���ŵ�·δ���£������һ�೵"),
    GarageWait(-1, "Ϊ����");

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
