package org.ldv.chatbot.config

import org.ldv.chatbot.model.dao.FournisseurIADAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.dao.RoleDAO
import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.ldv.chatbot.model.entity.FournisseurIA
import org.ldv.chatbot.model.entity.ModeleIA
import org.ldv.chatbot.model.entity.Role
import org.ldv.chatbot.model.entity.Utilisateur
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataInitializer(
    private val fournisseurIADAO: FournisseurIADAO,
    private val modeleIADAO: ModeleIADAO,
    private val roleDAO: RoleDAO,
    private val utilisateurDAO: UtilisateurDAO,
    private val passwordEncoder: PasswordEncoder
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

        // Les rôles
        val roles = roleDAO.findAll()
        if (roles.isEmpty()) {
            roleDAO.save(Role(nom = "Utilisateur", nomLogique = "ROLE_USER"))
            roleDAO.save(Role(nom = "Administrateur", nomLogique = "ROLE_ADMIN"))
        }

        val roleAdmin = roleDAO.findAll().first { it.nomLogique == "ROLE_ADMIN" }
        val roleUser = roleDAO.findAll().first { it.nomLogique == "ROLE_USER" }

        // Les utilisateurs (comptes de test : uniquement pour un usage local)
        val utilisateurs = utilisateurDAO.findAll()
        if (utilisateurs.isEmpty()) {
            utilisateurDAO.save(
                Utilisateur(
                    prenom = "Timothée",
                    nom = "Moulin",
                    email = "admin@email.com",
                    motDePasse = passwordEncoder.encode("admin123")!!,
                    role = roleAdmin
                )
            )
            utilisateurDAO.save(
                Utilisateur(
                    nom = "Prane",
                    prenom = "Doli",
                    email = "doli.prane@email.com",
                    motDePasse = passwordEncoder.encode("user123")!!,
                    role = roleUser
                )
            )
        }
    }
}
