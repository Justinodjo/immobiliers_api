package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.ConsentementRGPD;
import com.immoservice.immobiliers.repository.ConsentementRGPDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.server.ResponseStatusException;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import com.immoservice.immobiliers.entities.UserInfo;
// import com.immoservice.immobiliers.repository.UserInfoRepository;


import java.util.List;
import java.util.Optional;

@Service
public class ConsentementRGPDService {

    @Autowired
    private ConsentementRGPDRepository consentementRepository;

    //  @Autowired
    // private UserInfoRepository userInfoRepository;

    public ConsentementRGPD saveConsentement(ConsentementRGPD consentement) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
    //     throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
    // }

    // String email = authentication.getName(); // ou récupérer via UserDetails

    // UserInfo userInfo = userInfoRepository.findByEmail(email)
    //         .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

    // consentement.setUserInfo(userInfo); // Associe l'utilisateur connecté au consentement
        return consentementRepository.save(consentement);
    }

    public List<ConsentementRGPD> getAllConsentements() {
        return consentementRepository.findAll();
    }

    public Optional<ConsentementRGPD> getConsentementById(Long id) {
        return consentementRepository.findById(id);
    }

    public void deleteConsentement(Long id) {
        consentementRepository.deleteById(id);
    }

    public List<ConsentementRGPD> getConsentementsByUserId(Long userId) {
        return consentementRepository.findByUserInfoId(userId);
    }
}
