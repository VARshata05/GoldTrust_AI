package com.goldtrust.jeweller;

import com.goldtrust.common.ResourceNotFoundException;
import com.goldtrust.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JewellerService {

    private final JewellerRepository jewellerRepository;

    @Transactional(readOnly = true)
    public JewellerDTO getCurrentJeweller() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jeweller jeweller = jewellerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Jeweller profile not found for user: " + principal.getUsername()));
        return JewellerDTO.fromEntity(jeweller);
    }

    @Transactional(readOnly = true)
    public List<JewellerDTO> getAllActiveJewellers() {
        return jewellerRepository.findAll().stream()
                .filter(Jeweller::isCertified)
                .map(JewellerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JewellerDTO getJewellerById(Long id) {
        Jeweller jeweller = jewellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jeweller not found with id: " + id));
        return JewellerDTO.fromEntity(jeweller);
    }
}

