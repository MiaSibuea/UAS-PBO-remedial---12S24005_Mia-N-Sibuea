package pbo.f01;

import java.util.List;
import java.util.Scanner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {

    private static EntityManagerFactory factory;
    private static EntityManager entityManager;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        factory = Persistence.createEntityManagerFactory("parkit-pu");
        entityManager = factory.createEntityManager();

        while (scanner.hasNextLine()) {

            String input = scanner.nextLine();

            if (input.equals("---")) break;

            String[] parts = input.split("#");

            switch (parts[0]) {

                case "area-add":
                    addArea(parts);
                    break;

                case "vehicle-add":
                    addVehicle(parts);
                    break;

                case "park":
                    parkVehicle(parts);
                    break;

                case "display-all":
                    displayAll();
                    break;
            }
        }

        entityManager.close();
        factory.close();
        scanner.close();
    }

    private static void addArea(String[] parts) {

        entityManager.getTransaction().begin();

        AreaParkir area = entityManager.find(AreaParkir.class, parts[1]);

        if (area == null) {
            area = new AreaParkir(parts[1],
                    Integer.parseInt(parts[2]),
                    parts[3]);
            entityManager.persist(area);
        }

        entityManager.getTransaction().commit();
    }

    private static void addVehicle(String[] parts) {

        entityManager.getTransaction().begin();

        Vehicle v = entityManager.find(Vehicle.class, parts[1]);

        if (v == null) {
            v = new Vehicle(parts[1], parts[2], parts[3]);
            entityManager.persist(v);
        }

        entityManager.getTransaction().commit();
    }

    private static void parkVehicle(String[] parts) {

        entityManager.getTransaction().begin();

        Vehicle v = entityManager.find(Vehicle.class, parts[1]);
        AreaParkir a = entityManager.find(AreaParkir.class, parts[2]);

        if (v != null && a != null) {

            if (v.getParkingArea() == null &&
                v.getType().equals(a.getAllowedType()) &&
                a.getVehicles().size() < a.getCapacity()) {

                v.setParkingArea(a);
                entityManager.merge(v);
            }
        }

        entityManager.getTransaction().commit();
    }

    private static void displayAll() {

        List<AreaParkir> areas = entityManager.createQuery(
                "SELECT a FROM AreaParkir a ORDER BY a.name",
                AreaParkir.class
        ).getResultList();

        for (AreaParkir area : areas) {

            List<Vehicle> vehicles = entityManager.createQuery(
                    "SELECT v FROM Vehicle v WHERE v.parkingArea.name = :name ORDER BY v.plateNumber",
                    Vehicle.class
            )
            .setParameter("name", area.getName())
            .getResultList();

            System.out.println(
                    area.getName() + " " +
                    area.getAllowedType() + " " +
                    area.getCapacity() + "|" +
                    vehicles.size()
            );

            for (Vehicle v : vehicles) {
                System.out.println(
                        v.getPlateNumber() + " " +
                        v.getOwner() + " " +
                        v.getType()
                );
            }
        }
    }
}