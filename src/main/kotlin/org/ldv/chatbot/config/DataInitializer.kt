package org.ldv.chatbot.config

import org.ldv.chatbot.model.dao.FournisseurIADAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.FournisseurIA
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer(
    private val fournisseurIADAO: FournisseurIADAO,
    private val modeleIADAO: ModeleIADAO
) {

    @Bean
    fun initData() = CommandLineRunner {

        // Fournisseurs : créés seulement s'ils n'existent pas déjà
        val mistral = fournisseurIADAO.findByNom("Mistral")
            ?: fournisseurIADAO.save(
                FournisseurIA(
                    nom = "Mistral",
                    cheminLogo = "/img/mistral.png",
                    description = "Fournisseur français de modèles d'intelligence artificielle."
                )
            )

        val gemini = fournisseurIADAO.findByNom("Gemini")
            ?: fournisseurIADAO.save(
                FournisseurIA(
                    nom = "Gemini",
                    cheminLogo = "/img/gemini.png",
                    description = "Modèles d'intelligence artificielle de Google."
                )
            )

        // Modèles à initialiser
        val modeles = listOf(
            ModeleIA(
                nom = "Gemini 3.8 Flash",
                identifiantTechnique = "gemini-3.8-flash",
                urlAPI = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent",
                description = "Modèle rapide et polyvalent.",
                cheminLogo = "/img/gemini.png",
                fournisseurIA = gemini
            ),
            ModeleIA(
                nom = "Gemini 3.5 Flash Lite",
                identifiantTechnique = "gemini-3.5-flash-lite",
                urlAPI = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent",
                description = "Modèle rapide pour les tâches simples.",
                cheminLogo = "/img/gemini.png",
                fournisseurIA = gemini
            ),
            ModeleIA(
                nom = "Mistral Small",
                identifiantTechnique = "mistral-small-latest",
                urlAPI = "https://api.mistral.ai/v1/chat/completions",
                description = "Modèle léger pour les conversations et les tâches courantes.",
                cheminLogo = "/img/mistral.png",
                fournisseurIA = mistral
            ),
            ModeleIA(
                nom = "Mistral Medium",
                identifiantTechnique = "mistral-medium-latest",
                urlAPI = "https://api.mistral.ai/v1/chat/completions",
                description = "Modèle plus avancé pour les tâches complexes.",
                cheminLogo = "/img/mistral.png",
                fournisseurIA = mistral
            )
        )

        // Éviter de recréer les modèles à chaque démarrage
        for (modele in modeles) {
            if (!modeleIADAO.existsByIdentifiantTechnique(modele.identifiantTechnique)) {
                modeleIADAO.save(modele)
            }
        }
    }
}