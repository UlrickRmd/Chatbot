package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var titre: String,

    @Column(nullable = false)
    var dateCreation: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var dateDerniereModification: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "modele_ia_id")
    var modeleIA: ModeleIA,

    // Propriétaire de la conversation (clé étrangère utilisateur_id)
    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    var utilisateur: Utilisateur
) {
    // Composition : les messages disparaissent avec la conversation
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("dateCreation ASC")
    var messages: MutableList<Message> = mutableListOf()
}