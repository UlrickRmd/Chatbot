package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage
import org.ldv.chatbot.service.IAServiceFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Controller
class HomeController(
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO,
    private val iaServiceFactory: IAServiceFactory
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
        try {
            // Sélection automatique du service
            val iaService = iaServiceFactory.getService(modele)

            // Appel du fournisseur correspondant
            val reponse = iaService.envoyerPrompt(prompt, modele)


            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l’appel au modèle ${modele.nom} : ${e.message}")
        }

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

    @PostMapping("/conversations/{id}/messages")
    fun envoyerMessage(
        @PathVariable id: Long,
        @RequestParam prompt: String
    ): String {

        val conversation = conversationDAO.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        // Enregistrement du message utilisateur
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversation
        )

        messageDAO.save(messageUtilisateur)

        try {
            val modele = conversation.modeleIA!!
            // Sélection automatique du service
            val iaService = iaServiceFactory.getService(modele)

            // Appel du fournisseur correspondant
            val reponse = iaService.envoyerPrompt(prompt, modele)


            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l'appel à ${conversation.modeleIA!!.nom} : ${e.message}")
        }

        conversation.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversation)

        return "redirect:/conversations/$id"
    }


    // Suppression d'une conversation (et de ses messages, par cascade)
    @PostMapping("/conversations/{id}/supprimer")
    fun supprimerConversation(
        @PathVariable id: Long,
        @RequestParam(required = false) retour: String?
    ): String {

        if (conversationDAO.existsById(id)) {
            conversationDAO.deleteById(id)
        }

        // Si on supprime une autre conversation que celle affichée, on reste dessus
        val idCourant = retour?.toLongOrNull()
        return if (idCourant != null && idCourant != id && conversationDAO.existsById(idCourant)) {
            "redirect:/conversations/$idCourant"
        } else {
            "redirect:/chat"
        }
    }

    // Page de connexion (Spring Security) : affichage uniquement.
    // Le traitement du formulaire (POST /login) est fait par Spring Security,
    // il ne faut pas créer de @PostMapping("/login") ici.
    @GetMapping("/login")
    fun login(model: Model): String {
        // Permet de masquer le header sur la page de connexion (voir base.html)
        model.addAttribute("pageConnexion", true)
        return "auth/login"
    }

    // Ancienne page de connexion : conservée, elle redirige maintenant vers /login
    @GetMapping("/connexion")
    fun connexion(): String {
        return "redirect:/login"
    }

    // Ancien traitement du formulaire de connexion (gardé en commentaire) :
    // il est remplacé par Spring Security (POST /login).
    /*
    @PostMapping("/connexion")
    fun traiterConnexion(
        @RequestParam email: String,
        model: Model
    ): String {
        model.addAttribute("erreur", "Identifiant ou mot de passe incorrect.")
        model.addAttribute("email", email)
        return "auth/connexion"
    }
    */

}
