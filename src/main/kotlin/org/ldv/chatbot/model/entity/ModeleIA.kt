package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
class ModeleIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    @Column(nullable = false)
    var identifiantTechnique: String,

    @Column(nullable = false, length = 500)
    var urlAPI: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    var cheminLogo: String,

    // plusieurs modèles pour 1 fournisseur
    @ManyToOne(optional = false)
    @JoinColumn(name = "fournisseur_ia_id")
    var fournisseurIA: FournisseurIA
) {
    // 1 modèle est utilisé par 0..* conversations
    @OneToMany(mappedBy = "modeleIA")
    var conversations: MutableList<Conversation> = mutableListOf()
}