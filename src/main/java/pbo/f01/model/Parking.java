/**
 * 12S24005 - MIA N SIBUEA
 */

package pbo.f01.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking")
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "allowed_type", nullable = false)
    private String allowedType;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("plateNumber ASC")
    private List<Vehicle> vehicles = new ArrayList<>();

    public Parking() {}

    public Parking(String name, int capacity, String allowedType) {
        this.name = name;
        this.capacity = capacity;
        this.allowedType = allowedType;
    }

    public boolean allowsType(String type) {
        return this.allowedType.equalsIgnoreCase(type);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int c) {
        this.capacity = c;
    }

    public String getAllowedType() {
        return allowedType;
    }

    public void setAllowedType(String t) {
        this.allowedType = t;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> v) {
        this.vehicles = v;
    }
}