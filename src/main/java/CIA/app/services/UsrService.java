package CIA.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import CIA.app.model.Usr;
<<<<<<< Updated upstream
=======
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.PartnerRepository;
import CIA.app.repositories.ServicesRepository;
>>>>>>> Stashed changes
import CIA.app.repositories.UsrRepository;

@Service
public class UsrService {
    @Autowired
    private UsrRepository usrRepository;
    @Autowired
<<<<<<< Updated upstream
    private PasswordEncoder passwordEncoder;

    public UsrService(UsrRepository usrRepository, PasswordEncoder passwordEncoder) {
        this.usrRepository = usrRepository;
        this.passwordEncoder = passwordEncoder;
=======
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final ServicesRepository servicesRepository;
    @Autowired
    private final CoursesDataRepository coursesDataRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PartnerRepository partnerRepository;


    public UsrService(UsrRepository usrRepository, PasswordEncoder passwordEncoder,
            ServicesRepository servicesRepository, CoursesDataRepository coursesDataRepository,
            TokenRepository tokenRepository, PartnerRepository partnerRepository) {
        this.usrRepository = usrRepository;
        this.passwordEncoder = passwordEncoder;
        this.servicesRepository = servicesRepository;
        this.coursesDataRepository = coursesDataRepository;
        this.tokenRepository = tokenRepository;
        this.partnerRepository = partnerRepository;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======

    /*
    public List<Partner> getNearestPartner(String email, String type, double maxDistance) {
        log.info("Variables: " + email + " " + type + " " + maxDistance);
        Usr user = findByEmail(email);
        if (user != null) {
            Map<Integer, Partner> map = getPartnersByTypeServicesNR(type);

            // log.info("Mapa: " + map.toString());
            for (Map.Entry<Integer, Partner> entry : map.entrySet()) {
                log.info("Entry: " + entry.getKey() + " - " + entry.getValue().getName().toString() + " - lat: "
                        + entry.getValue().getLat() + " - lon " + entry.getValue().getLon());
            }

            if (map.isEmpty())
                return null;
            List<Partner> partners = new ArrayList<>(map.values());

            log.info("Lista: " + partners.toString());
            for (Partner p : partners) {
                log.info("Partner: " + p.getName().toString() + " - lat: " + p.getLat() + " - lon " + p.getLon());
            }

            List<Partner> near = calculateDistance(user, partners, maxDistance);

            log.info("Cercanos: " + near.toString());
            return near;

        }
        return null;
    }
 */
    public List<Partner>  getNearestPartner(String email, String type, double maxDistance){

        Usr user = findByEmail(email);
        List<Partner> partners = new ArrayList<>();
        if(user == null){
        partners = getPartnerByService(type);
        if(partners == null || partners.isEmpty()){
            return Collections.emptyList();
        }
    }

        return calculateDistance(user,partners, maxDistance);
    }

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
 /*
    public Map<Integer, Partner> getPartnersByTypeServicesNR(String type) {

        List<Services> sR = servicesRepository.getServicesByType(type);
        if (sR == null || sR.isEmpty())
            return Collections.emptyMap();

        Map<Integer, Partner> partnersMapWR = new HashMap<>();

        for (Services s : sR) {
            if (s.getPartner() == null)
                continue;

            for (Partner p : s.getPartner()) {
                if (p == null || p.getId() == null)
                    continue;

                partnersMapWR.putIfAbsent(p.getId(), p);
            }
        }

        return partnersMapWR;
    }
 */
    public List<Partner> getPartnerByService(String type){
        if(type.equals("SOAT")){
            return partnerRepository.getPartnersBySoat();
        }else if(type.equals("techno")){
        return partnerRepository.getPartnersByTechno();
    }
    return partnerRepository.getCIA();
    }
    
    private List<Partner> calculateDistance(Usr user, List<Partner> partners, double maxDistance) {
        double userLat = user.getLat();
        double userLon = user.getLon();

        return partners.stream()
                .filter(partner -> partner.getLat() != null && partner.getLon() != null)
                .map(partner -> new AbstractMap.SimpleEntry<>(partner,
                        approxToMeters(userLat, userLon, partner.getLat(), partner.getLon())))
                .filter(entry -> entry.getValue() <= maxDistance)
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();


    }

    private static double approxToMeters(double lat1, double lon1, double lat2, double lon2) {
        double ky = 111_320.0;
        double kx = 111_320.0 * Math.cos(Math.toRadians((lat1 + lat2) / 2.0));
        double dx = (lon2 - lon1) * kx;
        double dy = (lat2 - lat1) * ky;
        return Math.hypot(dx, dy);
    }

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

    public List<CoursesData> getCoursesByUser(String email){
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            List<CoursesData> courses = coursesDataRepository.getCoursesByUser(user.getId());
            if(courses != null && !courses.isEmpty()){
                return courses;
            }
            return null;
        }

        return null;
    }

    public CoursesData registerUserToCourse(String email, CoursesData course){
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            CoursesData existingCourse = coursesDataRepository.findById(course.getId()).orElse(null);
            if (existingCourse != null) {
                boolean enrolled = user.getCourses().stream().anyMatch(c -> c.getId().equals(existingCourse.getId()));
                if(!enrolled){
                    if(existingCourse.getParcialCapacity() + 1 <= existingCourse.getCapacity()){
                        existingCourse.setParcialCapacity(existingCourse.getParcialCapacity() + 1);
                        user.getCourses().add(existingCourse);
                        usrRepository.save(user);
                        return existingCourse;
                    }
                    throw new IllegalStateException("Cupo lleno");
                }
                throw new IllegalStateException("Inscriba un curso diferente");
            }
            throw new IllegalStateException("Curso no encontrado");
        }
        return null;
    }

    public CoursesData deleteUserFromCourse(String email, CoursesData course){
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            CoursesData existingCourse = coursesDataRepository.findById(course.getId()).orElse(null);
            if (existingCourse != null) {
                boolean enrolled = user.getCourses().stream().anyMatch(c -> c.getId().equals(existingCourse.getId()));
                if(enrolled){
                    if(existingCourse.getParcialCapacity() -1 >= 0){
                        existingCourse.setParcialCapacity(existingCourse.getParcialCapacity() -1);
                        user.getCourses().removeIf(c -> c.getId().equals(existingCourse.getId()));
                        usrRepository.save(user);
                        return existingCourse;
                    }
                    throw new IllegalStateException("Error en capacidad parcial del curso");
                }
                throw new IllegalStateException("El usuario no está inscrito en ese curso");
            }
            throw new IllegalStateException("Curso no encontrado");
        }
        return null;
    }

    public List<CoursesData> getAllCourses(String email){
        Usr usr = usrRepository.findByEmail(email);
        if (usr != null) {
            return coursesDataRepository.findAll();
        }
        return null;
    }
>>>>>>> Stashed changes
}
