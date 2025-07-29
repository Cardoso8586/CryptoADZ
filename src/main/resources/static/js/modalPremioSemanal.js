document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("modalPremio");
  const closeBtn = document.getElementById("fecharModal");
  const confirmarBtn = document.getElementById("confirmarPremio");
  const valorPremioSpan = document.getElementById("valorPremio");
  const mensagemPremio = document.getElementById("mensagemPremio");

  const usuarioId = getUsuarioLogadoId();

  // Função para pegar o ID do usuário logado
  function getUsuarioLogadoId() {
    const meta = document.querySelector('meta[name="user-id"]');
    return meta ? parseInt(meta.getAttribute('content')) : null;
  }

  // Buscar prêmio pendente do usuário
  fetch(`/api/usuario/${usuarioId}/premio-pendente`)
    .then(res => res.json())
    .then(valor => {
      if (valor > 0) {
        valorPremioSpan.textContent = valor;

        // Mensagem personalizada com base no valor
        if (valor >= 100) {
          mensagemPremio.textContent = "🎉 Parabéns! Você ficou em 1º lugar!";
        } else if (valor >= 50) {
          mensagemPremio.textContent = "🥈 Excelente! Você ficou em 2º lugar!";
        } else if (valor >= 25) {
          mensagemPremio.textContent = "🥉 Muito bom! Você ficou em 3º lugar!";
        } else {
          mensagemPremio.textContent = "🎁 Você recebeu um prêmio especial!";
        }

        modal.style.display = "block";
      }
    })
    .catch(error => {
      console.error("Erro ao buscar prêmio:", error);
    });

  // Fechar modal ao clicar no X
  closeBtn.onclick = () => {
    modal.style.display = "none";
  };

  // Confirmar resgate do prêmio
  confirmarBtn.onclick = () => {
    fetch(`/api/usuario/${usuarioId}/confirmar-premio`, { method: "POST" })
      .then(res => {
        if (res.ok) {
          Swal.fire({
            icon: 'success',
            title: 'Sucesso!',
            text: 'Prêmio resgatado com sucesso!',
            timer: 4000,
            timerProgressBar: true,
            showConfirmButton: false
          });
          modal.style.display = "none";
        } else {
          Swal.fire({
            icon: 'warning',
            title: 'Atenção!',
            text: 'Erro ao resgatar o prêmio.',
            confirmButtonText: 'OK'
          });
        }
      })
      .catch(error => {
        Swal.fire({
          icon: 'error',
          title: 'Erro!',
          text: 'Ocorreu um problema ao tentar resgatar o prêmio.',
          confirmButtonText: 'OK'
        });
        console.error("Erro ao confirmar prêmio:", error);
      });
  };

  // Fechar modal clicando fora dele
  window.onclick = (event) => {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  };
});

