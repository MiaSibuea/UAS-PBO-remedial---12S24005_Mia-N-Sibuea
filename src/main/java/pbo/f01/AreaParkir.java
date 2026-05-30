package pbo.f01;
 
/**
 * Driver class utama
 * Nama: [Mia Nathania Sibuea]
 * Nim: [12S24005]
 */

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_area")
public class AreaParkir {

    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "allowed_type", nullable = false)
    private String allowedType;

    @OneToMany(mappedBy = "parkingArea")
    private List<Vehicle> vehicles = new ArrayList<>();

    public AreaParkir() {
    }

    public AreaParkir(String name, int capacity, String allowedType) {
        this.name = name;
        this.capacity = capacity;
        this.allowedType = allowedType;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getAllowedType() {
        return allowedType;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setAllowedType(String allowedType) {
        this.allowedType = allowedType;
    }

    public int getCurrentVehicleCount() {
        return vehicles.size();
    }

    @Override
    public String toString() {
        return name + " " + allowedType + " " + capacity + "|" + getCurrentVehicleCount();
    }
}