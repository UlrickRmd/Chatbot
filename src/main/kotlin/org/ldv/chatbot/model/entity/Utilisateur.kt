package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
class Utilisateur(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String = "",

    var prenom: String,

    var nom: String,

    @Column(nullable = false, length = 510)
    var motDePasse: String = "",

    @Column(nullable = false)
    var actif: Boolean = true,

    @ManyToOne
    @JoinColumn(name = "role_id")
    var role: Role,

    // TODO : association avec Conversation (plus tard)
) {
}
