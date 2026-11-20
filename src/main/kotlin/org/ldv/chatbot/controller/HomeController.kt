package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Controller
class HomeController(
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO
) {

    // Page d'accueil : le chatbot est directement utilisable
    @GetMapping("/")
    fun accueil(model: Model): String {
        // Retire la conversation courante
        model.addAttribute("conversation", null)
        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("messages", emptyList<Message>())
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateDerniereModificationDesc()
        )
        return "index"
    }

    // Page historique
    @GetMapping("/historique")
    fun historique(model: Model): String {
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateDerniereModificationDesc()
        )
        return "utilisateur/historique"
    }

    // Lancement d'une conversation depuis la page d'accueil
    @PostMapping("/conversations")
    fun creerConversation(
        @RequestParam prompt: String,
        @RequestParam idModele: Long
    ): String {

        val texte = prompt.trim()
        if (texte.isEmpty()) {
            return "redirect:/"
        }

        val modele = modeleIADAO.findById(idModele)
            .orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle IA introuvable")
            }

        val conversation = conversationDAO.save(
            Conversation(
                titre = texte.take(50),
                modeleIA = modele
            )
        )

        messageDAO.save(
            Message(
                contenu = texte,
                typeMessage = TypeMessage.UTILISATEUR,
                conversation = conversation
            )
        )

        return "redirect:/conversations/${conversation.id}"
    }

    // Affichage d'une conversation (sur la même page que l'accueil)
    @GetMapping("/conversations/{id}")
    fun afficherConversation(
        @PathVariable id: Long,
        model: Model
    ): String {

        val conversation = conversationDAO.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateDerniereModificationDesc()
        )
        model.addAttribute("conversation", conversation)
        model.addAttribute("messages", messageDAO.findByConversationIdOrderByDateCreationAsc(id))
        return "utilisateur/chat"
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