package CIA.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import CIA.app.model.Usr;
import CIA.app.model.Vehicle;
import CIA.app.repositories.VehicleRepository;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    /* 
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UsrService usrService;

    @InjectMocks
    private VehicleService vehicleService;

    private Usr makeUser(String email, int id) {
        Usr u = new Usr();
        u.setId(id);
        u.setEmail(email);
        u.setName("User " + id);
        return u;
    }

    private Vehicle makeVehicle(String plateLowerOrUpper) {
        Vehicle v = new Vehicle();
        v.setId(10);
        v.setType("car");
        v.setPlate(plateLowerOrUpper);
        v.setSoatRateType("A");
        v.setTechnoClassification("Particular");
        v.setSoatExpiration(Date.valueOf("2026-01-01"));
        v.setTechnoExpiration(Date.valueOf("2026-06-01"));
        return v;
    }

    @Test
    void saveVehicle_success_newPlate_assignsUser_and_UppercasesPlate_andPersists() {
        // given
        String email = "owner@exa.co";
        Vehicle req = makeVehicle("abc123"); // llega en minúscula
        Usr owner = makeUser(email, 1);

        when(vehicleRepository.findByPlate("abc123")).thenReturn(null); // no existe
        when(usrService.findByEmail(email)).thenReturn(owner);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(inv -> inv.getArgument(0));

        Vehicle out = vehicleService.saveVehicle(email, req);

        assertNotNull(out);
        assertEquals("ABC123", out.getPlate(), "La placa debe guardarse en MAYÚSCULAS");
        assertNotNull(out.getUsr());
        assertEquals(owner.getEmail(), out.getUsr().getEmail(), "Debe asignar el usuario dueño");
        verify(vehicleRepository).findByPlate("abc123");
        verify(usrService).findByEmail(email);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void getByPlate_success() {
        Vehicle found = makeVehicle("XYZ999");
        when(vehicleRepository.findByPlate("XYZ999")).thenReturn(found);

        Vehicle out = vehicleService.getByPlate("XYZ999");

        assertNotNull(out);
        assertEquals("XYZ999", out.getPlate());
        verify(vehicleRepository).findByPlate("XYZ999");
    }

    @Test
    void getVehicles_success_returnsUsersVehicles() {
        String email = "fleet@exa.co";
        Usr owner = makeUser(email, 2);

        Vehicle v1 = makeVehicle("AAA111");
        Vehicle v2 = makeVehicle("BBB222");
        v1.setUsr(owner);
        v2.setUsr(owner);
        owner.setVehicles(List.of(v1, v2));

        when(usrService.findByEmail(email)).thenReturn(owner);

        List<Vehicle> out = vehicleService.getVehicles(email);

        assertNotNull(out);
        assertEquals(2, out.size());
        assertEquals("AAA111", out.get(0).getPlate());
        assertEquals("BBB222", out.get(1).getPlate());
        verify(usrService).findByEmail(email);
        verifyNoInteractions(vehicleRepository);
    }

    @Test
    void deleteByPlate_success_findsByOwnerAndPlate_deletesAndReturnsVehicle() {
        String email = "owner@exa.co";
        String plate = "CCC333";
        Vehicle v = makeVehicle(plate);

        when(vehicleRepository.findByUsr_EmailAndPlate(email, plate)).thenReturn(v);
        Vehicle out = vehicleService.deleteByPlate(email, plate);

        assertNotNull(out);
        assertEquals(plate, out.getPlate());
        verify(vehicleRepository).findByUsr_EmailAndPlate(email, plate);
        verify(vehicleRepository).delete(v);
    }
        */
}
