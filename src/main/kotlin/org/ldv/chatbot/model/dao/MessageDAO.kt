package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageDAO : JpaRepository<Message, Long> {
    fun findByConversationIdOrderByDateCreationAsc(conversationId: Long): List<Message>
}