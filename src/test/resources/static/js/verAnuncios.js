document.addEventListener('DOMContentLoaded', () => {
  const botaoAnuncios = document.querySelectorAll('.ver-anuncio');
  const vistos = JSON.parse(localStorage.getItem('anunciosVistos') || '{}');
  let anuncioEmContagem = false;

  function atualizarEstadoDosBotoes() {
    const agora = Date.now();
    botaoAnuncios.forEach(botao => {
      const id = botao.dataset.id;
      const estaBloqueado = id in vistos && agora < vistos[id];
      botao.disabled = estaBloqueado || anuncioEmContagem;
      botao.classList.toggle('visto', estaBloqueado);
    });
  }

  atualizarEstadoDosBotoes();

  botaoAnuncios.forEach(link => {
    link.addEventListener('click', async e => {
      e.preventDefault();
      if (link.disabled || anuncioEmContagem) return;

      anuncioEmContagem = true;
      atualizarEstadoDosBotoes();

      const anuncioId = link.dataset.id;
      const url = link.dataset.url;
      const display = document.getElementById(`contador-${anuncioId}`);

      window.open(url, '_blank');

      let resposta;
      try {
        // ✅ Correção aqui: chamada para GET /tempo/{id}
        const resTempo = await fetch(`/api/visualizacoes/tempo/${anuncioId}`, {
          credentials: 'include'
        });

        if (!resTempo.ok) throw new Error();

        resposta = await resTempo.json(); // { tempo }

      } catch (err) {
        alert('Falha ao obter o tempo do anúncio.');
        anuncioEmContagem = false;
        atualizarEstadoDosBotoes();
        return;
      }

      let restante = resposta.tempo ?? 30; // segundos
      const desbloqueioTimestamp = Date.now() + (restante * 1000);
      vistos[anuncioId] = desbloqueioTimestamp;
      localStorage.setItem('anunciosVistos', JSON.stringify(vistos));

      link.classList.add('visto');
      link.disabled = true;

      const iniciarContagem = (regressiva) => {
        const tick = async () => {
          if (regressiva <= 0) {
            try {
              // Registrar visualização
              const resRegistrar = await fetch(`/api/visualizacoes/registrar-visualizacao/${anuncioId}`, {
                method: 'POST',
                credentials: 'include'
              });

              if (!resRegistrar.ok) throw new Error();

              // Buscar tokens creditados
              try {
                const resTokens = await fetch(`/api/visualizacoes/tokens-creditados/${anuncioId}`, {
                  credentials: 'include'
                });

                if (!resTokens.ok) throw new Error();

                const finalData = await resTokens.json();
                const tokensFinais = finalData.tokensCreditados ?? 0;

                display.innerHTML = `<strong style="color:green;">Você recebeu ${tokensFinais.toFixed(2)} tokens!</strong>`;
                document.title = `+${tokensFinais.toFixed(2)} Tokens creditados ✔`;

              } catch (e) {
                display.innerHTML = `<strong style="color:orange;">Tokens creditados ✔ (quantia oculta ou indisponível)</strong>`;
                document.title = `Tokens creditados ✔`;
              }

            } catch (e) {
              display.innerHTML = `<strong style="color:red;">Falha ao registrar visualização e creditar tokens.</strong>`;
              document.title = `Erro`;
            }

            setInterval(() => {
              // Pode deixar vazio ou para futuras ações
            }, 3000);

            anuncioEmContagem = false;
            atualizarEstadoDosBotoes();
            registrarMissaoAssistir();
            setTimeout(() => {
              location.reload();
            }, 1000);

          } else {
            display.textContent = `Tempo restante: ${regressiva}s`;
            document.title = `${regressiva}s - Por favor, mantenha esta página aberta`;
            setTimeout(() => tick(--regressiva), 1000);
          }
        };

        tick();
      };

      iniciarContagem(restante);
    });
  });
});

function registrarMissaoAssistir() {
  const usuarioId = document.querySelector('meta[name="user-id"]')?.content;
  if (!usuarioId) {
    console.error('Usuário não encontrado para missão de assistir.');
    return;
  }

  fetch(`/missoes/incrementar-assistir/${usuarioId}`, {
    method: 'POST'
  })
  .then(res => res.text())
  .then(msg => {
    console.log('Missão de assistir registrada:', msg);
    carregarStatusMissoes(); // Atualiza contadores na tela
  })
  .catch(err => {
    console.error('Erro ao registrar missão de assistir:', err);
  });
}
