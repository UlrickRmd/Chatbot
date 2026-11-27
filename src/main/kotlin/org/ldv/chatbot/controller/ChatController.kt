package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.Utilisateur
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatController(

    private val conversationDAO: ConversationDAO,

    private val modeleIADAO: ModeleIADAO,

    private val utilisateurDAO: UtilisateurDAO
) {

    // Récupère l'entité Utilisateur à partir de l'authentification (email = identifiant)
    private fun getUtilisateurConnecte(authentication: Authentication): Utilisateur {
        val email = authentication.name
        return utilisateurDAO
            .findByEmailIgnoreCase(email)
            ?: throw UsernameNotFoundException("Utilisateur introuvable")
    }

    // Page d'accueil : liste des conversations de l'utilisateur connecté
    @GetMapping("/chat")
    fun accueil(model: Model, authentication: Authentication): String {

        // Recupere l'utilisateur de la session
        val utilisateur = this.getUtilisateurConnecte(authentication)
        model.addAttribute("utilisateur", utilisateur)

        // Retire la conversation courante
        model.addAttribute("conversation", null)

        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("messages", emptyList<Message>())
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByUtilisateurOrderByDateDerniereModificationDesc(utilisateur)
        )

        return "utilisateur/chat"
    }

}
