package org.ldv.chatbot.service

import org.ldv.chatbot.dto.mistral.MistralChatRequest
import org.ldv.chatbot.dto.mistral.MistralChatResponse
import org.ldv.chatbot.dto.mistral.MistralMessage
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class MistralService(
    private val restClientBuilder: RestClient.Builder,

    @Value("\${mistral.api.key}")
    private val apiKey: String

) : IAService {

    override fun getFournisseur(): String = "Mistral"

    override fun envoyerPrompt(prompt: String, modele: ModeleIA): String {

        // Étape 1 : message de l'utilisateur
        val message = MistralMessage(
            role = "user",
            content = prompt
        )

        // Étape 2 : requête pour Mistral
        val request = MistralChatRequest(
            model = modele.identifiantTechnique,
            messages = listOf(message)
        )

        // Étape 3 : envoi de la requête HTTP POST
        val client = restClientBuilder.build()

        val response = client.post()
            .uri(modele.urlAPI)
            .header("Authorization", "Bearer $apiKey")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(MistralChatResponse::class.java)

        // Étape 4 : récupération du texte
        val reponse = response?.choices?.firstOrNull()?.message?.content

        return reponse ?: "Aucune réponse reçue de Mistral."
    }
}