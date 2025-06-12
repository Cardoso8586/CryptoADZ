document.addEventListener("DOMContentLoaded", function () {
  function formatarTempo(segundos) {
    const horas = Math.floor(segundos / 3600);
    const minutos = Math.floor((segundos % 3600) / 60);
    const segundosRestantes = segundos % 60;

    if (horas > 0) {
      return `${horas}h ${minutos}min`;
    } else if (minutos > 0) {
      return `${minutos}min`;
    } else {
      return `${segundosRestantes}s`;
    }
  }

  function mensagemBloqueio(segundos) { 
    const simbolo = '🔒'; // símbolo cadeado
    if (segundos <= 120) {
      return `${simbolo} Wait ${formatarTempo(segundos)}`;
    } else if (segundos <= 900) {
      return `${simbolo} Wait ${formatarTempo(segundos)}`;
    } else if (segundos <= 3600) {
      return `${simbolo} Retry in ${formatarTempo(segundos)}`;
    } else if (segundos <= 10800) {
      return `${simbolo} Try later`;
    } else {
      return `${simbolo} Come back later`;
    }
  }

  function atualizarBloqueio(link, username, anuncioId) {
    fetch(`/api/visualizacoes/status-bloqueio?username=${username}&anuncioId=${anuncioId}`)
      .then(response => response.json())
      .then(data => {
        if (data.bloqueado) {
          link.classList.add('bloqueado');
          const mensagem = mensagemBloqueio(data.tempoRestante);
          link.textContent = mensagem;
          link.style.backgroundColor = '#ff4d4d';
          link.removeAttribute('href');
          link.style.pointerEvents = 'none';
        } else {
          link.classList.remove('bloqueado');
          link.textContent = "✅ Click here to view";
          link.setAttribute('href', link.getAttribute('data-url'));
          link.style.backgroundColor = '#4CAF50';
          link.style.pointerEvents = 'auto';
        }
      })
      .catch(error => {
        console.error('❌ Erro ao verificar status de bloqueio:', error);
      });
  }

  function verificarTodosPeriodicamente() {
    const links = document.querySelectorAll('.ver-anuncio');

    links.forEach(link => {
      const username = link.getAttribute('data-username');
      const anuncioId = link.getAttribute('data-id');

      if (!username || !anuncioId) return;

      atualizarBloqueio(link, username, anuncioId);
    });
  }

  verificarTodosPeriodicamente(); // Execução imediata
  setInterval(verificarTodosPeriodicamente, 60000); // A cada 60 segundos
});

