package pbo.f01;

/**
 * 12S24005 - MIA N SIBUEA
 */

import javax.persistence.*;
import pbo.f01.model.Parking;
import pbo.f01.model.Vehicle;

import java.util.List;
import java.util.Scanner;
import java.util.logging.*;

public class App {

    private static EntityManager em;

    public static void main(String[] args) {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        Logger.getLogger("com.zaxxer").setLevel(Level.OFF);
        Logger.getLogger("").setLevel(Level.OFF);

        EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("park-it-pu");
        em = emf.createEntityManager();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("#");
            String command = parts[0].trim();

            switch (command) {
                case "area-add":
                    if (parts.length >= 4) {
                        areaAdd(parts[1].trim(),
                                Integer.parseInt(parts[2].trim()),
                                parts[3].trim());
                    }
                    break;

                case "vehicle-add":
                    if (parts.length >= 4) {
                        vehicleAdd(parts[1].trim(),
                                   parts[2].trim(),
                                   parts[3].trim());
                    }
                    break;

                case "park":
                    if (parts.length >= 3) {
                        park(parts[1].trim(), parts[2].trim());
                    }
                    break;

                case "display-all":
                    displayAll();
                    break;

                default:
                    break;
            }
        }

        em.close();
        emf.close();
        scanner.close();
    }

    private static void areaAdd(String name, int capacity, String allowedType) {
        if (findAreaByName(name) != null) return;

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(new Parking(name, capacity, allowedType));
        tx.commit();
    }

    private static void vehicleAdd(String plateNumber, String owner, String type) {
        if (findVehicleByPlate(plateNumber) != null) return;

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(new Vehicle(plateNumber, owner, type));
        tx.commit();
    }

    private static void park(String plateNumber, String areaName) {
        Vehicle vehicle = findVehicleByPlate(plateNumber);
        if (vehicle == null) return;

        Parking area = findAreaByName(areaName);
        if (area == null) return;

        if (!area.allowsType(vehicle.getType())) return;

        Long count = em.createQuery(
                "SELECT COUNT(v) FROM Vehicle v WHERE v.parking = :area",
                Long.class)
            .setParameter("area", area)
            .getSingleResult();

        if (count >= area.getCapacity()) return;

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        vehicle.setParking(area);
        em.merge(vehicle);
        tx.commit();
    }

    private static void displayAll() {
        List<Parking> areas = em.createQuery(
                "SELECT a FROM Parking a ORDER BY a.name ASC",
                Parking.class)
            .getResultList();

        for (Parking area : areas) {
            Long count = em.createQuery(
                    "SELECT COUNT(v) FROM Vehicle v WHERE v.parking = :area",
                    Long.class)
                .setParameter("area", area)
                .getSingleResult();

            System.out.println(area.getName() + " " + area.getAllowedType()
                + " " + area.getCapacity() + "|" + count);

            List<Vehicle> vehicles = em.createQuery(
                    "SELECT v FROM Vehicle v WHERE v.parking = :area" +
                    " ORDER BY v.plateNumber ASC",
                    Vehicle.class)
                .setParameter("area", area)
                .getResultList();

            for (Vehicle v : vehicles) {
                System.out.println(v);
            }
        }
    }

    private static Parking findAreaByName(String name) {
        try {
            return em.createQuery(
                    "SELECT a FROM Parking a WHERE a.name = :name",
                    Parking.class)
                .setParameter("name", name)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Vehicle findVehicleByPlate(String plate) {
        try {
            return em.createQuery(
                    "SELECT v FROM Vehicle v WHERE v.plateNumber = :plate",
                    Vehicle.class)
                .setParameter("plate", plate)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}