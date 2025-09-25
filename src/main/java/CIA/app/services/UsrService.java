package CIA.app.services;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import CIA.app.model.Partner;
import CIA.app.model.Services;
//import CIA.app.model.Services;
import CIA.app.model.Usr;
import CIA.app.repositories.ServicesRepository;
//import CIA.app.repositories.ServicesRepository;
import CIA.app.repositories.UsrRepository;
import lombok.extern.slf4j.Slf4j;
import CIA.app.model.Token;
import CIA.app.repositories.TokenRepository;

@Slf4j
@Service
public class UsrService {
    @Autowired
    private final UsrRepository usrRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final ServicesRepository servicesRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public UsrService(UsrRepository usrRepository, PasswordEncoder passwordEncoder, ServicesRepository servicesRepository, TokenRepository tokenRepository) {
        this.usrRepository = usrRepository;
        this.passwordEncoder = passwordEncoder;
        this.servicesRepository = servicesRepository;
        this.tokenRepository = tokenRepository;
    }

    public Usr registUser(Usr user) {
        Usr existingUser = usrRepository.findByEmail(user.getEmail());
        Usr existingIdentification = usrRepository.findByIdentification(user.getIdentification());
        if (existingUser != null || existingIdentification != null) {
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usrRepository.save(user);
    }

    public Usr login(String email, String password) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public Usr findByEmail(String email) {
        return usrRepository.findByEmail(email);
    }

    public Usr deleteByEmail(String email) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            usrRepository.delete(user);
            return user;
        }
        return null;
    }

    public Usr update(String currentEmail, Usr req) {
        Usr user = usrRepository.findByEmail(currentEmail);
        if (user != null) {
            if (!currentEmail.equals(req.getEmail())) {
                Usr comNemail = usrRepository.findByEmail(req.getEmail());
                if (comNemail == null) {
                    user.setEmail(req.getEmail());
                }
            }
            user.setName(req.getName());
            return usrRepository.save(user);
        }
        return null;
    }

    public List<Partner> getNearestPartner(String email, String type, double maxDistance){
        log.info("Variables: " + email + " " + type + " " + maxDistance);
        Usr user = findByEmail(email);
        if (user != null) {
            Map<Integer, Partner> map = getPartnersByTypeServicesNR( type);

            //log.info("Mapa: " + map.toString());
            for(Map.Entry<Integer, Partner> entry : map.entrySet()){
                log.info("Entry: " + entry.getKey() + " - " + entry.getValue().getName().toString() + " - lat: " + entry.getValue().getLat() + " - lon " + entry.getValue().getLon());
            }

            if(map.isEmpty()) return null;
            List<Partner> partners = new ArrayList<>(map.values());

            log.info("Lista: " + partners.toString());
            for(Partner p : partners){
                log.info("Partner: " + p.getName().toString() + " - lat: " + p.getLat() + " - lon " + p.getLon());
            }

            List<Partner> near = calculateDistance(user, partners, maxDistance);
            
            log.info("Cercanos: " + near.toString());
            return near;
            
    public String generarToken(String email) {
        int tokenInt = (int) (Math.random() * 900000) + 100000;
        String token = String.valueOf(tokenInt);

        Token verificarToken = new Token();
        verificarToken.setToken(token);
        verificarToken.setEmail(email);
        verificarToken.setExpiracionToken(LocalDateTime.now().plusMinutes(5));
        tokenRepository.save(verificarToken);
        return token;
    }

    public boolean verificarToken(String token, String correo) {
        Token verificarToken = tokenRepository.buscarPorToken(token, correo);
        if (verificarToken == null) {
            return false;
        }
        if (verificarToken.getExpiracionToken().isBefore(LocalDateTime.now())) {
            return false;
        }
        tokenRepository.eliminarToken(token);
        return true;
    }

    public Usr changePassword(Usr usr) {
        Usr user = usrRepository.findByEmail(usr.getEmail());
        if (user != null) {
            String hashedPassword = BCrypt.hashpw(usr.getPassword(), BCrypt.gensalt(12));
            user.setPassword(hashedPassword);
            return usrRepository.save(user);
        }
        return null;
    }

    public Map<Integer, Partner> getPartnersByTypeServicesNR(String type){
        
        List<Services> sR = servicesRepository.getServicesByType(type);
            if(sR==null || sR.isEmpty()) return Collections.emptyMap();

            Map<Integer, Partner> partnersMapWR = new HashMap<>();

            for(Services s : sR){
                if (s.getPartner() == null) continue;

                for (Partner p : s.getPartner()) {
                    if (p == null || p.getId() == null) continue;

                    partnersMapWR.putIfAbsent(p.getId(), p);
                }
            }

            return partnersMapWR;
    }

    private List<Partner> calculateDistance(Usr user, List<Partner> partners, double maxDistance) {
        double userLat = user.getLat();
        double userLon = user.getLon();

        return partners.stream()
            .filter(partner -> partner.getLat() != null && partner.getLon() != null)
            .map(partner -> new AbstractMap.SimpleEntry<>(partner, approxToMeters(userLat, userLon, partner.getLat(), partner.getLon())))
            .filter(entry -> entry.getValue() <= maxDistance)
            .sorted(Comparator.comparingDouble(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .toList();

        //log.info("calculateDistance: userLat={}, userLon={}, maxDistance={}", userLat, userLon, maxDistance);
        //log.info("Partners recibidos: {}", partners.size());

        // return partners.stream()
        //     .peek(p -> log.info("IN -> id={} name={} lat={} lon={}",
        //         p.getId(), p.getName(), p.getLat(), p.getLon()))
        //     .filter(p -> {
        //         boolean ok = p.getLat() != null && p.getLon() != null;
        //         if (!ok) log.info("DESCARTE (coords nulas) -> id={} name={}", p.getId(), p.getName());
        //         return ok;
        //     })
        //     .map(p -> new AbstractMap.SimpleEntry<>(
        //         p, approxMetersBogota(userLat, userLon, p.getLat(), p.getLon())
        //     ))
        //     .peek(e -> log.info("DIST -> id={} name={} d≈{}m",
        //         e.getKey().getId(), e.getKey().getName(), String.format("%.1f", e.getValue())))
        //     .filter(e -> {
        //         boolean ok = e.getValue() <= maxDistance;
        //         if (!ok) log.info("FUERA_RADIO -> id={} name={} d={}m > {}m",
        //                 e.getKey().getId(), e.getKey().getName(),
        //                 String.format("%.1f", e.getValue()), String.format("%.1f", maxDistance));
        //         return ok;
        //     })

        //     .sorted(Comparator.comparingDouble(Map.Entry::getValue))
        //     .peek(new java.util.function.Consumer<Map.Entry<Partner, Double>>() {
        //         int rank = 0;
        //         @Override public void accept(Map.Entry<Partner, Double> e) {
        //             log.info("RANK #{} -> id={} name={} d≈{}m",
        //                     ++rank, e.getKey().getId(), e.getKey().getName(), String.format("%.1f", e.getValue()));
        //         }
        //     })

        //     .map(Map.Entry::getKey)
        //     .toList();
        
    }

    private static double approxToMeters(double lat1, double lon1, double lat2, double lon2) {
        double ky = 111_320.0;
        double kx = 111_320.0 * Math.cos(Math.toRadians((lat1 + lat2) / 2.0));
        double dx = (lon2 - lon1) * kx;
        double dy = (lat2 - lat1) * ky;
        return Math.hypot(dx, dy);
      
    public Usr updatePassword(String email, String currentPassword, String newPassword) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            if (BCrypt.checkpw(currentPassword, user.getPassword())) {
                String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
                user.setPassword(hashedNewPassword);
                return usrRepository.save(user);
            }
        }
        return null;
    }
}
