document.addEventListener("DOMContentLoaded", function () {

    console.log("PakiSanIA est chargé !");

    // Affiche toujours le dernier message de la conversation
    const zoneMessages = document.querySelector(".chat-content");
    if (zoneMessages) {
        zoneMessages.scrollTop = zoneMessages.scrollHeight;
    }

});