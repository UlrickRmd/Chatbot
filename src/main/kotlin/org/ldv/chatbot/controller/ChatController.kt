package org.ldv.chatbot.controller

import org.apache.logging.log4j.message.Message
import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.service.IAServiceFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatController(
    private val iaServiceFactory: IAServiceFactory,
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO
) {
    // Page d'accueil : liste des conversations
   @GetMapping("/chat")
    fun accueil(model: Model): String {

        // Retire la conversation courante
        model.addAttribute("conversation", null)

        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("messages", emptyList<Message>())
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateDerniereModificationDesc()
        )

        return "utilisateur/chat"
    }

}
