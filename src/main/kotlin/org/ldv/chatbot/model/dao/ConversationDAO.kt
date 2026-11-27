package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Utilisateur
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationDAO : JpaRepository<Conversation, Long> {

    // Uniquement les conversations d'un utilisateur, de la plus récente à la plus ancienne
    fun findAllByUtilisateurOrderByDateDerniereModificationDesc(utilisateur: Utilisateur): List<Conversation>

    // Renvoie null si la conversation n'existe pas OU n'appartient pas à l'utilisateur
    fun findByIdAndUtilisateur(id: Long, utilisateur: Utilisateur): Conversation?
}
