package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    // Nom affiché dans les templates
    var nom: String,

    // Nom utilisé par Spring Security
    var nomLogique: String,
) {
}
