package com.example.appgidritexmonitoring.component;

import com.example.appgidritexmonitoring.entity.*;
import com.example.appgidritexmonitoring.repository.*;
import com.example.appgidritexmonitoring.service.ImportService;
import com.example.appgidritexmonitoring.service.DeviceInitialSaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservoirRepository reservoirRepository;
    private final GateRepository gateRepository;
    private final ImportService importService;
    private final DeviceInitialSaver deviceInitialSaver;


    public DataLoader(RoleRepository roleRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      ReservoirRepository reservoirRepository,
                      GateRepository gateRepository,
                      ImportService importService,
                      DeviceInitialSaver deviceInitialSaver,
                      PiezometerRepository piezometerRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.reservoirRepository = reservoirRepository;
        this.gateRepository = gateRepository;
        this.importService = importService;
        this.deviceInitialSaver = deviceInitialSaver;
    }

    @Value("${app.admin.username}")
    private String username;

    @Value("${app.admin.firstName}")
    private String firstName;

    @Value("${app.admin.lastName}")
    private String lastName;

    @Value("${app.admin.password}")
    private String password;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String modelType;

    @Value("${app.run-type}")
    private String runType;

    @Override
    public void run(String... args) throws Exception {
        if (Objects.equals("create", modelType)) {
            addReservoirs();
            addUsers();
            addGates();
            saveDevices();
        }

        if (Objects.equals("refresh", runType)){
            updateDevices();
        }

        importDevicesToRamFromDB();
        readFromFiles();
    }

    private void saveDevices(){
        deviceInitialSaver.saveDevices();
    }

    private void updateDevices(){
        deviceInitialSaver.updateDevices();
    }

    private void readFromFiles(){
        importService.read();
    }

    private void importDevicesToRamFromDB(){
        importService.importPiezometersByDeviceDatasource();
        importService.importWaterLevelGaugesByDeviceDatasource();
        importService.importSpillwaysByDeviceDatasource();
        importService.importWaterFlowMetersByDeviceDatasource();
        importService.importHydrologicalStationsByDeviceDatasource();
        importService.importDamBodyDevicesByDeviceDatasource();
        importService.importCrackGaugesByDeviceDatasource();
        importService.importPlumbsByDeviceDatasource();
        importService.importTiltmetersByDeviceDatasource();
        importService.importDeformometersByDeviceDatasource();
    }

    private void addUsers() {
        List<User> users = new ArrayList<>();
        Role adminRole = addSuperAdminRole();
        User admin = User.builder()
                .role(adminRole)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .enabled(true)
                .password(passwordEncoder.encode(password))
                .reservoirs(getReservoirByUser(username))
                .build();


        users.add(admin);
        userRepository.saveAll(users);
    }

    private Role addSuperAdminRole() {
        Role superAdmin = Role.builder()
                .description("SuperAdmin description")
                .name("SuperAdmin")
                .build();
        return roleRepository.save(superAdmin);
    }


    private void addGates() {

        List<Reservoir> reservoirs = reservoirRepository.findAll();
        Reservoir xisorak = reservoirs.stream().filter(reservoir -> reservoir.getName().startsWith("Ҳисорак")).findFirst().orElseThrow();
        Reservoir oxangaron = reservoirs.stream().filter(reservoir -> reservoir.getName().startsWith("Оҳангарон")).findFirst().orElseThrow();
        Reservoir andijan = reservoirs.stream().filter(reservoir -> reservoir.getName().startsWith("Андижон")).findFirst().orElseThrow();
        Reservoir charvak = reservoirs.stream().filter(reservoir -> reservoir.getName().startsWith("Чорвок")).findFirst().orElseThrow();

        List<Gate> gates = new ArrayList<>();
        List<Integer> gatesNumOfXisorak = List.of(0, 1, 2, 3, 6, 7, 19, 27, 31, 34, 35, 5, 9, 8, 10, 11, 13, 15, 17, 21, 23, 25, 28, 29, 32, 30, 33);
        List<String> gatesNameOfOxangaron = List.of("Pk1+20", "Pk3+30", "Pk5+90", "Pk6+30", "Pk8+70", "Pk10+20", "Pk11+51", "Pk11+90", "Pk12+50", "Pk13+33,7", "Pk14+90", "Pk16+00", "Pk17+36,5");

        for (Integer numbersOfGate : gatesNumOfXisorak) {
            Gate gate = Gate.builder()
                    .ordinal(Double.valueOf(numbersOfGate))
                    .name(String.valueOf(numbersOfGate))
                    .reservoir(xisorak)
                    .build();
            gates.add(gate);
        }

        for (int i = 0; i < gatesNameOfOxangaron.size(); i++) {
            Gate gate = Gate.builder()
                    .reservoir(oxangaron)
                    .name(gatesNameOfOxangaron.get(i))
                    .ordinal((double) (i+1))
                    .build();
            gates.add(gate);
        }

        for (int i = 1; i <= 33; i++) {
            Gate gate = Gate.builder()
                    .reservoir(andijan)
                    .name(i + " section")
                    .ordinal((double) i)
                    .build();
            gates.add(gate);
        }

        List<Gate> extraGatesOfAndijan = List.of(
                Gate.make(30.5, "30/31 section", andijan),
                Gate.make(34D, "SUP section", andijan),
                Gate.make(35D, "Grunt section", andijan),
                Gate.make(36D, "Tektomik zona section", andijan),
                Gate.make(37D, "SUL section", andijan),
                Gate.make(31.5D, "31/32 section", andijan),
                Gate.make(33.5, "33/SUP section", andijan),
                Gate.make(1.5D, "1/2 section", andijan),
                Gate.make(5.5, "5/6 section", andijan),
                Gate.make(29.5, "29/30 section", andijan),
                Gate.make(24.5, "24/25 section", andijan),
                Gate.make(25.5, "25/26 section", andijan),
                Gate.make(6.5, "6/7 section", andijan),
                Gate.make(4.5, "4/5 section", andijan),
                Gate.make(3.5, "3/4 section", andijan),
                Gate.make(7.5, "7/8 section", andijan),
                Gate.make(9.5, "9/10 section", andijan),
                Gate.make(11.5, "11/12 section", andijan),
                Gate.make(13.5, "13/14 section", andijan),
                Gate.make(12.5, "12/13 section", andijan),
                Gate.make(14.5, "14/15 section", andijan),
                Gate.make(15.5, "15/16 section", andijan),
                Gate.make(16.5, "16/17 section", andijan),
                Gate.make(17.5, "17/18 section", andijan),
                Gate.make(18.5, "18/19 section", andijan),
                Gate.make(21.5, "21/22 section", andijan),
                Gate.make(22.5, "22/23 section", andijan),
                Gate.make(23.5, "23/24 section", andijan),
                Gate.make(19.5, "19/20 section", andijan),
                Gate.make(27.5, "27/28 section", andijan),
                Gate.make(2.5, "2/3 section", andijan),
                Gate.make(1.3, "1/SUL section", andijan),
                Gate.make(38D, "upper bief section", andijan),
                Gate.make(39D, "lower bief section", andijan));

        gates.addAll(extraGatesOfAndijan);

        List<Gate> gatesOfCharvak = List.of(
                Gate.make(0D, "the right side section", charvak),
                Gate.make(1D, "the left side section", charvak),
                Gate.make(0.5D, "Dam crest section", charvak),
                Gate.make(2D, "2 section", charvak),
                Gate.make(5D, "5 section", charvak),
                Gate.make(23D, "23 section", charvak),
                Gate.make(25D, "25 section", charvak),
                Gate.make(28D, "28 section", charvak),
                Gate.make(1D, "1 gallery section", charvak),
                Gate.make(2D, "2 gallery section", charvak),
                Gate.make(6D, "6 gallery section", charvak),
                Gate.make(7D, "7 gallery section", charvak),
                Gate.make(8D, "8 section", charvak),
                Gate.make(9D, "9 section", charvak),
                Gate.make(12D, "12 section", charvak),
                Gate.make(14D, "14 section", charvak),
                Gate.make(15D, "15 section", charvak),
                Gate.make(17D, "17 section", charvak),
                Gate.make(17.5D, "Concrete plug section", charvak),
                Gate.make(19D, "19 section", charvak),
                Gate.make(20D, "20 section", charvak),
                Gate.make(7D, "7 section", charvak),
                Gate.make(11D, "11 section", charvak),
                Gate.make(13D, "13 section", charvak),
                Gate.make(16D, "16 section", charvak),
                Gate.make(18D, "18 section", charvak),
                Gate.make(21D, "21 section", charvak),
                Gate.make(29D, "Transportni shtolniya section", charvak),
                Gate.make(30D, "Sempoterna section", charvak),
                Gate.make(31D, "1 shtolniya section", charvak),
                Gate.make(32D, "2 shtolniya section", charvak),
                Gate.make(36D, "6 shtolniya section", charvak),
                Gate.make(37D, "7 shtolniya section", charvak)
        );

        gates.addAll(gatesOfCharvak);

        gateRepository.saveAll(gates);
    }

    private void addReservoirs() {
        Set<Reservoir> reservoirs = new HashSet<>();
        Reservoir reservoir = Reservoir.builder()
                .name("Ҳисорак сув омбори")
                .location("Қашқадарё вилояти")
                .build();
        reservoirs.add(reservoir);

        Reservoir reservoir2 = Reservoir.builder()
                .name("Оҳангарон сув омбори")
                .location("Тошкент вилояти")
                .build();
        reservoirs.add(reservoir2);

        Reservoir reservoir3 = Reservoir.builder()
                .name("Андижон сув омбори")
                .location("Андижон вилояти")
                .build();
        reservoirs.add(reservoir3);

        Reservoir reservoir4 = Reservoir.builder()
                .name("Чорвок сув омбори")
                .location("Тошкент вилояти")
                .build();
        reservoirs.add(reservoir4);

        reservoirRepository.saveAll(reservoirs);
    }

    private Set<Reservoir> getReservoirByUser(String username){
        List<Reservoir> reservoirs = reservoirRepository.findAllByOrderByNameDesc();
        return switch (username) {
            case "charvak" -> Set.of(reservoirs.get(0));
            case "xisorak" -> Set.of(reservoirs.get(1));
            case "akhangaran" -> Set.of(reservoirs.get(2));
            case "andijan" -> Set.of(reservoirs.get(3));
            default -> new HashSet<>(reservoirs);
        };
    }

//ПТС,ПДС,ПНГС,ПЛПС



}
