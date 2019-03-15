package vo;

import java.util.Vector;

public class Lane{
    private int laneIndex;
    private Vector<Car> cars;

    public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }

    public Vector<Car> getCars() {
        return cars;
    }

    public void setCars(Vector<Car> cars) {
        this.cars = cars;
    }

}
