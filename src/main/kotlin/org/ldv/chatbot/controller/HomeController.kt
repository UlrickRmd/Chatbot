package org.ldv.chatbot.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class HomeController {

    // Page d'accueil
    @GetMapping("/")
    fun accueil(): String {
        return "index"
    }

    // Page de connexion
    @GetMapping("/connexion")
    fun connexion(): String {
        return "auth/connexion"
    }

    // Traitement provisoire du formulaire
    @PostMapping("/connexion")
    fun traiterConnexion(
        @RequestParam email: String,
        @RequestParam password: String,
        model: Model
    ): String {

        model.addAttribute(
            "message",
            "Le formulaire fonctionne, mais l'authentification n'est pas encore activée."
        )

        return "auth/connexion"
    }
}