package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
class FournisseurIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    var cheminLogo: String
) {
    // 1 fournisseur propose 0..* modèles
    @OneToMany(mappedBy = "fournisseurIA")
    var modeles: MutableList<ModeleIA> = mutableListOf()
}