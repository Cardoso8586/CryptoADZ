/**
 * 
 */
document.addEventListener("DOMContentLoaded", function () {
  fetch("/api/anuncio/quantidade")
    .then(res => res.json())
    .then(data => {
      document.getElementById("quantidadeAnuncios").textContent = data.total;
    })
    .catch(err => {
      console.error("Erro ao carregar quantidade de anúncios:", err);
    });
});
