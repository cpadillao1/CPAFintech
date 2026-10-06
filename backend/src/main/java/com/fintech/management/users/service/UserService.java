package com.fintech.management.users.service;

import com.fintech.audit.aspect.Audit;
import com.fintech.eod.exception.BusinessRuleException;
import com.fintech.management.branches.domain.BranchEntity;
import com.fintech.management.branches.repository.BranchRepository;
import com.fintech.management.control_system.domain.ControlSystemEntity;
import com.fintech.management.control_system.repository.ControlSystemRepository;
import com.fintech.management.functionalities.domain.RoleEntity;
import com.fintech.management.functionalities.domain.FunctionalityEntity;
import com.fintech.security.JwtService;
import com.fintech.management.users.domain.UserEntity;
import com.fintech.management.users.dto.LoginRequest;
import com.fintech.management.users.dto.LoginResponse;
import com.fintech.management.users.dto.UserDTO;
import com.fintech.management.users.dto.UserRegistrationRequest;
import com.fintech.management.users.mapper.UserMapper;
import com.fintech.management.users.repository.RoleRepository;
import com.fintech.management.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final ControlSystemRepository controlSystemRepository;

    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Audit(action = "USER_CREATE", module = "USERS")
    @Transactional
    public UserDTO registerUser(UserRegistrationRequest request) {

        //  Validar unicidad del email (Regla de Oro en Fintech)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El correo electrónico " + request.getEmail() + " ya está registrado.", HttpStatus.BAD_REQUEST);
        }
        //  Generar el LOGIN (Regla: primera letra de firstName + lastName en minúsculas)
        String generatedLogin = generateLogin(request.getFirstName(), request.getLastName());

        //  Resolver colisión (ej: si jperez existe, buscar jperez1, jperez2...)
        String finalLogin = resolveLoginCollision(generatedLogin);

        //  Buscar sucursal
        BranchEntity branch = branchRepository.findByCode(request.getBranchCode())
                .orElseThrow(() -> new BusinessRuleException("Branch not found: " + request.getBranchCode(), HttpStatus.NOT_FOUND));

        //  Buscamos el objeto RoleEntity "USER" en la BD
        //RoleEntity defaultRole = roleRepository.findByName("ADMIN") // Cambia "USER" por "ADMIN"
        //        .orElseThrow(() -> new RuntimeException("Error crítico: El rol ADMIN no existe en la base de datos."));

        //  Mapear datos básicos (Podemos hacerlo manual o con el Mapper)
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setLogin(finalLogin);
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setBranch(branch);

        // ASIGNACIÓN DINÁMICA DE ROLES
        if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
            for (String roleName : request.getRoleNames()) {
                RoleEntity role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BusinessRuleException("El rol " + roleName + " no existe.", HttpStatus.NOT_FOUND));
                userEntity.getRoles().add(role);
            }
        } else {
            // Opcional: Si no envían roles, asignar "USER" por defecto
            RoleEntity defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new BusinessRuleException("Rol por defecto no encontrado", HttpStatus.NOT_FOUND));
            userEntity.getRoles().add(defaultRole);
        }

        // Finalmente guardas la instancia
        return userMapper.toDto(userRepository.save(userEntity));
    }

    @PreAuthorize("hasAuthority('USER_QUERY')")
    @Audit(action = "USER_QUERY", module = "USERS")
    @Transactional(readOnly = true)
    public UserDTO getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::toDto)
                .orElseThrow(() -> new BusinessRuleException("User not found", HttpStatus.NOT_FOUND));
    }


    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Audit(action = "USER_UPDATE", module = "USERS")
    @Transactional
    public UserDTO updateUser(UUID id, UserRegistrationRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        // Validaciones manuales básicas
        if (request.getFirstName() == null || request.getFirstName().isBlank()) throw new BusinessRuleException("El nombre es obligatorio", HttpStatus.BAD_REQUEST);
        if (request.getLastName() == null || request.getLastName().isBlank()) throw new BusinessRuleException("El apellido es obligatorio", HttpStatus.BAD_REQUEST);
        if (request.getEmail() == null || request.getEmail().isBlank()) throw new BusinessRuleException("El email es obligatorio", HttpStatus.BAD_REQUEST);

        // Validar si el email ya existe en otro usuario
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El correo electrónico ya está registrado.", HttpStatus.BAD_REQUEST);
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // 🔐 PASSWORD OPCIONAL: Solo si el admin escribió algo
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 8) {
                throw new BusinessRuleException("La nueva contraseña debe tener al menos 8 caracteres.", HttpStatus.BAD_REQUEST);
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getBranchCode() != null) {
            BranchEntity branch = branchRepository.findByCode(request.getBranchCode())
                    .orElseThrow(() -> new BusinessRuleException("Sucursal no encontrada", HttpStatus.NOT_FOUND));
            user.setBranch(branch);
        }

        return userMapper.toDto(userRepository.save(user));
    }

    // Metodo auxiliar para la lógica del login
    private String generateLogin(String firstName, String lastName) {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            throw new BusinessRuleException("Nombre y apellido son necesarios para generar el login", HttpStatus.BAD_REQUEST);
        }

        String rawLogin = firstName.charAt(0) + lastName;
        // Normalizar: quitar tildes, eñes y pasar a minúsculas
        String normalized = java.text.Normalizer.normalize(rawLogin, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("\\s+", "");
    }

    private String resolveLoginCollision(String baseLogin) {
        String candidate = baseLogin;
        int counter = 1;

        // Bucle: Mientras el login exista en la BD, seguimos incrementando el número
        while (userRepository.existsByLogin(candidate)) {
            candidate = baseLogin + counter;
            counter++;
        }

        return candidate;
    }

    @Audit(action = "LOGIN", module = "AUTH")
    public LoginResponse login(LoginRequest request) {
        // Get business date and system status
        ControlSystemEntity system = controlSystemRepository.findById(1)
                .orElseThrow(() -> new BadCredentialsException("Critical error: System configuration not found"));


        // 1. Validate credentials, branch and status Active
        UserEntity user = userRepository.findByLoginAndBranchIdAndActiveTrue(request.login(), request.branchId())
                .orElseThrow(() -> new BusinessRuleException("Incorrect credentials or the user does not have access to this branch.", HttpStatus.BAD_REQUEST));

        // 2. Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessRuleException("Invalid credentials.", HttpStatus.UNAUTHORIZED);
        }

        // 3. RECOLECCIÓN DE CÓDIGOS (Jerarquía completa para el TreeView)
        List<String> authorities = user.getRoles().stream()
                .flatMap(role -> role.getFunctionalities().stream())
                .map(FunctionalityEntity::getCode)
                .distinct()
                .toList();

        // Generamos el token pasando la lista de authorities
        String token = jwtService.createToken(user.getLogin(), user.getEmail(), authorities);

        // Format date
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return LoginResponse.builder()
                .token(token)
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .branchId(user.getBranch().getId())
                .branchName(user.getBranch().getName()) // Ya viene cargado gracias al EntityGraph
                .roles(authorities)
                .businessDate(system.getBusinessDate().format(formatter))
                .systemStatus(system.getStatus())
                .build();
    }

}
