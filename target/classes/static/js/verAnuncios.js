document.addEventListener('DOMContentLoaded', () => {
  const botaoAnuncios = document.querySelectorAll('.ver-anuncio');
  const vistos = JSON.parse(localStorage.getItem('anunciosVistos') || '{}');
  let anuncioEmContagem = false;
  let contagemAtiva = false;
  let cancelado = false;
  let janelaAnuncio = null;
  let checagemJanela = null;

  
  const modal = document.getElementById('anuncioModal');
  const contadorModal = document.getElementById('contadorModal');
  const fecharModal = document.getElementById('fecharModal');
  const tituloAnuncio = document.getElementById('tituloAnuncio');
  const imagemAnuncio = document.getElementById('imagemAnuncio');
  const linkAnuncio = document.getElementById('linkAnuncio');

  fecharModal.onclick = () => {
    if (contagemAtiva) {
      cancelarContagem('Contagem cancelada pelo usuário. Tokens não serão creditados.');
	  
    } else {
      modal.style.display = 'none';
    }
  };

  function cancelarContagem(mensagem) {
    cancelado = true;
    anuncioEmContagem = false;
    contagemAtiva = false;

    if (janelaAnuncio && !janelaAnuncio.closed) {
      janelaAnuncio.close();
    }

    if (checagemJanela) {
      clearInterval(checagemJanela);
      checagemJanela = null;
    }

	Swal.fire({
	  		    icon: 'erro',
	  		    title: 'Erro',
	  		    text: mensagem,
	  		    timer: 5000,
	  		    timerProgressBar: true,
	  		    showConfirmButton: false
	  		  });
    modal.style.display = 'none';
    atualizarEstadoDosBotoes();
    setTimeout(() => location.reload(), 5000);
  }

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
      contagemAtiva = true;
      cancelado = false;
      atualizarEstadoDosBotoes();

      const anuncioId = link.dataset.id;
      const url = link.dataset.url;
      const titulo = link.dataset.titulo || 'Aguardando Visualização...';
      const imagem = link.dataset.imagem;

      // Preencher dados no modal
      tituloAnuncio.textContent = titulo;
      if (imagem) {
        imagemAnuncio.src = imagem;
        imagemAnuncio.style.display = 'block';
      } else {
        imagemAnuncio.style.display = 'none';
      }
      linkAnuncio.href = url;
      linkAnuncio.textContent = url;

      // Abrir janela do anúncio
      janelaAnuncio = window.open(url, '_blank');
      if (!janelaAnuncio) {
       // alert('Falha ao abrir o anúncio. Verifique seu bloqueador de pop-ups.');
	
        cancelarContagem('Falha ao abrir anúncio. Contagem cancelada.');
        return;
      }

      modal.style.display = 'block';
      contadorModal.textContent = `Tempo restante: ...`;

      let resposta;
      try {
        const resTempo = await fetch(`/api/visualizacoes/tempo/${anuncioId}`, { credentials: 'include' });
        if (!resTempo.ok) throw new Error();
        resposta = await resTempo.json();
      } catch (err) {
       alert('Falha ao obter o tempo do anúncio.');
		
        cancelarContagem('Erro ao obter tempo do anúncio.');
        return;
      }

      let restante = resposta.tempo ?? 30;
      const desbloqueioTimestamp = Date.now() + (restante * 1000);
      vistos[anuncioId] = desbloqueioTimestamp;
      localStorage.setItem('anunciosVistos', JSON.stringify(vistos));
      link.classList.add('visto');
      link.disabled = true;

      // Checar se a janela foi fechada antes
      checagemJanela = setInterval(() => {
        if (!janelaAnuncio || janelaAnuncio.closed) {
          cancelarContagem('Janela do anúncio fechada antes do tempo. Tokens não serão creditados.');
        }
      }, 500);

      const iniciarContagem = (regressiva) => {
        const tick = async () => {
          if (cancelado) {
            clearInterval(checagemJanela);
            checagemJanela = null;
            return;
          }

          if (regressiva <= 0) {
            clearInterval(checagemJanela);
            checagemJanela = null;

            try {
              const resRegistrar = await fetch(`/api/visualizacoes/registrar-visualizacao/${anuncioId}`, {
                method: 'POST',
                credentials: 'include'
              });

              if (!resRegistrar.ok) throw new Error();

              try {
                const resTokens = await fetch(`/api/visualizacoes/tokens-creditados/${anuncioId}`, {
                  credentials: 'include'
                });

                if (!resTokens.ok) throw new Error();

                const finalData = await resTokens.json();
                const tokensFinais = finalData.tokensCreditados ?? 0;

                tituloAnuncio.textContent = 'Concluído!';
                contadorModal.innerHTML = `<strong style="color:green;">Você recebeu ${tokensFinais.toFixed(2)} tokens!</strong>`;
                document.title = `+${tokensFinais.toFixed(2)} Tokens creditados ✔`;

              } catch (e) {
                contadorModal.innerHTML = `<strong style="color:orange;">Tokens creditados ✔ (quantia oculta)</strong>`;
                document.title = `Tokens creditados ✔`;
              }

            } catch (e) {
              contadorModal.innerHTML = `<strong style="color:red;">Falha ao registrar visualização.</strong>`;
              document.title = `Erro`;
            }

            anuncioEmContagem = false;
            contagemAtiva = false;
            atualizarEstadoDosBotoes();
            registrarMissaoAssistir();

            setTimeout(() => location.reload(), 1000);

          } else {
            contadorModal.textContent = `Tempo restante: ${regressiva}s`;
            document.title = `${regressiva}s - Mantenha a página aberta`;
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
    console.error('Usuário não encontrado para missão.');
    return;
  }

  fetch(`/api/missoes/incrementar-assistir/${usuarioId}`, {
    method: 'POST'
  })
    .then(res => res.text())
    .then(msg => {
      console.log('Missão registrada:', msg);
      carregarStatusMissoes?.();
    })
    .catch(err => {
      console.error('Erro ao registrar missão:', err);
    });
}

//============================================================  otimo  ============================================================