document.addEventListener("DOMContentLoaded", function () {
  fetch("/api/anuncio/quantidade-cliks")
    .then(res => res.json())
    .then(data => {
      document.getElementById("quantidadeCliks").textContent = data.total;
    })
    .catch(err => {
      console.error("Erro ao carregar quantidade de anúncios:", err);
    });
});