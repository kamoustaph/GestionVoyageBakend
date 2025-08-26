package moustapha.teach.gestionVoyage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    @GetMapping("/user")
    public Principal getUser(Principal principal) {
        return principal;
    }

    @GetMapping("/success")
    public String success() {
        return "Authentication successful!";
    }
}