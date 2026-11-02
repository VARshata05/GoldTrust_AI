package com.goldtrust.auth;

import com.goldtrust.common.BadRequestException;
import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.customer.Customer;
import com.goldtrust.customer.CustomerRepository;
import com.goldtrust.jeweller.Jeweller;
import com.goldtrust.jeweller.JewellerRepository;
import com.goldtrust.security.JwtTokenProvider;
import com.goldtrust.security.UserPrincipal;
import com.goldtrust.user.Role;
import com.goldtrust.user.RoleName;
import com.goldtrust.user.RoleRepository;
import com.goldtrust.user.User;
import com.goldtrust.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final JewellerRepository jewellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Long customerId = customerRepository.findByUserId(user.getId())
                .map(Customer::getId).orElse(null);
        Long jewellerId = jewellerRepository.findByUserId(user.getId())
                .map(Jeweller::getId).orElse(null);

        return AuthResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .roles(roles)
                .customerId(customerId)
                .jewellerId(jewellerId)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address is already in use");
        }

        Set<Role> roles = new HashSet<>();
        RoleName roleName = "JEWELLER".equalsIgnoreCase(request.getRole()) ?
                RoleName.ROLE_JEWELLER : RoleName.ROLE_CUSTOMER;

        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
        roles.add(userRole);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .roles(roles)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);
        Long customerId = null;
        Long jewellerId = null;

        if (roleName == RoleName.ROLE_CUSTOMER) {
            String customerNumber = "GT-CUST-" + String.format("%06d", savedUser.getId());
            Customer customer = Customer.builder()
                    .user(savedUser)
                    .customerNumber(customerNumber)
                    .addressLine1(request.getAddressLine1())
                    .city(request.getCity())
                    .state(request.getState())
                    .postalCode(request.getPostalCode())
                    .country("India")
                    .kycStatus("VERIFIED")
                    .riskScore(BigDecimal.valueOf(1.00))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            Customer savedCustomer = customerRepository.save(customer);
            customerId = savedCustomer.getId();
        } else if (roleName == RoleName.ROLE_JEWELLER) {
            String jewellerCode = "JWL-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            Jeweller jeweller = Jeweller.builder()
                    .user(savedUser)
                    .jewellerCode(jewellerCode)
                    .businessName(request.getBusinessName() != null ? request.getBusinessName() : request.getFirstName() + "'s Gold Boutique")
                    .licenseNumber(request.getLicenseNumber() != null ? request.getLicenseNumber() : "BIS-LIC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .bisHallmarkCenterId(request.getBisCenterId() != null ? request.getBisCenterId() : "AHC-9901")
                    .contactPerson(request.getFirstName() + " " + request.getLastName())
                    .phone(request.getPhoneNumber())
                    .email(request.getEmail())
                    .address(request.getAddressLine1())
                    .city(request.getCity())
                    .state(request.getState())
                    .rating(BigDecimal.valueOf(4.80))
                    .verificationStatus("ACTIVE")
                    .certified(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            Jeweller savedJeweller = jewellerRepository.save(jeweller);
            jewellerId = savedJeweller.getId();
        }

        // Authenticate immediately and return AuthResponse
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(jwt)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                .roles(List.of(roleName.name()))
                .customerId(customerId)
                .jewellerId(jewellerId)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            throw new BadRequestException("User is not authenticated");
        }

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Long customerId = customerRepository.findByUserId(user.getId())
                .map(Customer::getId).orElse(null);
        Long jewellerId = jewellerRepository.findByUserId(user.getId())
                .map(Jeweller::getId).orElse(null);

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .roles(roles)
                .customerId(customerId)
                .jewellerId(jewellerId)
                .build();
    }
}
