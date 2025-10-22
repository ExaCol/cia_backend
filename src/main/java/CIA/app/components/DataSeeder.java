package CIA.app.components;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import CIA.app.model.Partner;
import CIA.app.model.SOAT_FARE;
import CIA.app.model.TECNO_FARE;
import CIA.app.model.Usr;
import CIA.app.repositories.PartnerRepository;
import CIA.app.repositories.SOAT_FARERepository;
import CIA.app.repositories.TECNO_FARERepository;
import CIA.app.repositories.UsrRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final PartnerRepository partnerRepository;
    private final UsrRepository usrRepository;
    private final PasswordEncoder passwordEncoder;
    private final SOAT_FARERepository  SOATRepository;
    private final TECNO_FARERepository TECNORepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedPartner();
        seedAdmin();
        seedEmployee();
        seedsoatFaresOnce();
        seedTecnoFaresUpsert();
    }

    private void seedPartner() {
        if (!partnerRepository.existsById(1)) {
            Partner r = new Partner();
            r.setName("CIA");
            r.setLat(4.1111111111111);
            r.setLon(4.2222222222222);
            r.setSoat(false);
            r.setTechno(false);
            try {
                partnerRepository.saveAndFlush(r);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                System.out.println("Data seeding failed: " + e.getMessage());
            }
        }
    }

    private void seedAdmin(){
         if (!usrRepository.existsById(1)) {
            Usr r = new Usr();
            r.setName("Admin");
            r.setIdentification("cc-1111111111");
            r.setEmail("admin@exa.co");
            r.setPassword(passwordEncoder.encode("Secreta123"));
            r.setLat(4.634269);
            r.setLon(-74.066388);
            r.setRole("Admin");
            try {
                usrRepository.saveAndFlush(r);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                System.out.println("Data seeding failed: " + e.getMessage());
            }
        }
    }

    private void seedEmployee(){
         if (!usrRepository.existsById(2)) {
            Usr r = new Usr();
            r.setName("Empleado");
            r.setIdentification("cc-2222222222");
            r.setEmail("empleado@exa.co");
            r.setPassword(passwordEncoder.encode("Secreta123"));
            r.setLat(4.634269);
            r.setLon(-74.066388);
            r.setRole("Empleado");
            try {
                usrRepository.saveAndFlush(r);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                System.out.println("Data seeding failed: " + e.getMessage());
            }
        }
    }

    private void seedsoatFaresOnce() {
        if (SOATRepository.count() == 0) {

            
            var all = List.of(
                soat(100,118200), soat(110,243700), soat(120,326600), soat(130,758600),
                soat(140,368100), soat(150,368100),

                soat(211,789900), soat(221,943100), soat(231,1106200),

                soat(212,949500), soat(222,1117100), soat(232,1269300),

                soat(310,885000), soat(320,1277900), soat(330,1615800),

                soat(410,995800), soat(420,1255400), soat(430,1505000),

                soat(511,445600), soat(521,542700), soat(531,633800),

                soat(512,590700), soat(522,675000), soat(532,751600),

                soat(611,794400), soat(621,1063300),

                soat(612,1013900), soat(622,1276700),

                soat(711,268200), soat(721,333000), soat(731,429300),

                soat(712,334800), soat(722,411200), soat(732,503500),

                soat(810,640300), soat(910,633000), soat(920,918000)
            );
            SOATRepository.saveAllAndFlush(all);
        }
        }

        private SOAT_FARE soat(int id, int price) {
        SOAT_FARE f = new SOAT_FARE();
        f.setId(id);
        f.setPrice(price);
        return f;
    }

    private void seedTecnoFaresUpsert() {
        // Datos originales (tipo, inicio, fin, precio)
        Object[][] rows = new Object[][]{
            {"Motos", 2023, 2025, 215541},
            {"Motos", 2018, 2022, 215841},
            {"Motos", 2009, 2017, 216141},
            {"Motos", 2008, 2008, 215841},

            {"Liviano Particular", 2023, 2025, 330341},
            {"Liviano Particular", 2018, 2022, 330741},
            {"Liviano Particular", 2009, 2017, 331041},
            {"Liviano Particular", 2008, 2008, 330741},

            {"Liviano Publico", 2023, 2025, 329841},
            {"Liviano Publico", 2018, 2022, 330141},
            {"Liviano Publico", 2009, 2017, 330441},
            {"Liviano Publico", 2008, 2008, 330141},

            {"Pesado Particular", 2023, 2025, 470741},
            {"Pesado Particular", 2018, 2022, 471041},
            {"Pesado Particular", 2009, 2017, 741241},
            {"Pesado Particular", 2008, 2008, 471041},

            {"Pesado Público", 2023, 2025, 470341},
            {"Pesado Público", 2018, 2022, 470541},
            {"Pesado Público", 2009, 2017, 470741},
            {"Pesado Público", 2008, 2008, 470541}
        };

        for (Object[] r : rows) {
            String type = (String) r[0];
            Integer start = (Integer) r[1];
            Integer end   = (Integer) r[2];
            int price     = (Integer) r[3];

            TECNORepository.findByTypeAndStartYearAndEndYear(type, start, end)
                .ifPresentOrElse(
                    existing -> {
                        if (existing.getPrice() != price) {
                            existing.setPrice(price);
                            TECNORepository.save(existing);
                        }
                    },
                    () -> {
                        TECNO_FARE f = new TECNO_FARE();
                        f.setType(type);
                        f.setStartYear(start);
                        f.setEndYear(end);
                        f.setPrice(price);
                        TECNORepository.save(f);
                    }
                );
        }
        TECNORepository.flush();
    }
}
