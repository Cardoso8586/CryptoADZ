document.addEventListener('DOMContentLoaded', () => {
  const sections = {
    verAnuncios: document.getElementById('VerAnuncios'),
    anunciar:    document.getElementById('Anunciar'),
    wallet:      document.getElementById('Wallet'),
    missoes:     document.getElementById('secaoMissoes')
  };

  function hideAll() {
    Object.values(sections).forEach(sec => {
      if(sec) sec.style.display = 'none';
    });
  }

  function show(name) {
    hideAll();
    if(sections[name]) sections[name].style.display = 'block';
  }

  document.getElementById('btnVerAnuncios')?.addEventListener('click', e => { e.preventDefault(); show('verAnuncios'); });
  document.getElementById('btnAnunciar')?.addEventListener('click', e => { e.preventDefault(); show('anunciar'); });
  document.getElementById('btnWallet')?.addEventListener('click', e => { e.preventDefault(); show('wallet'); });
  document.getElementById('btnMissoes')?.addEventListener('click', e => {
    e.preventDefault();
    show('missoes');
    carregarStatusMissoes();
  });

  // Eventos para os botões de reivindicar (se existirem)
  const btnColetarCadastrar = document.getElementById('btnColetarCadastrar');
  if (btnColetarCadastrar) {
    btnColetarCadastrar.addEventListener('click', e => {
      e.preventDefault();
      reivindicarCadastro();
    });
  }

  const btnColetarAssistir = document.getElementById('btnColetarAssistir');
  if (btnColetarAssistir) {
    btnColetarAssistir.addEventListener('click', e => {
      e.preventDefault();
      reivindicarAssistir();
    });
  }

  show('verAnuncios');

  function getUsuarioLogadoId() {
    const meta = document.querySelector('meta[name="user-id"]');
    return meta ? meta.getAttribute('content') : null;
  }

  function atualizarUIStatusMissoes(status) {
    console.log('Status recebido:', status);

    const assistirCount  = status.contadorAssistir  ?? 0;
    const cadastrarCount = status.contadorCadastrar ?? 0;

    const statusAssistir = document.getElementById('statusAssistir');
    const statusCadastro = document.getElementById('statusCadastro');
    if(statusAssistir) statusAssistir.innerText = assistirCount >= 20 ? '✅' : `${assistirCount}/20`;
    if(statusCadastro) statusCadastro.innerText = cadastrarCount >= 1 ? '✅' : `${cadastrarCount}/1`;

    const btnColetarAssistir = document.getElementById('btnColetarAssistir');
    const btnColetarCadastrar = document.getElementById('btnColetarCadastrar');

    let textoAssistir = document.getElementById('textoReivindicadoAssistir');
    if (btnColetarAssistir && !textoAssistir) {
      textoAssistir = document.createElement('span');
      textoAssistir.id = 'textoReivindicadoAssistir';
      textoAssistir.style.color = 'green';
      textoAssistir.style.marginLeft = '10px';
      btnColetarAssistir.parentNode.appendChild(textoAssistir);
    }

    let textoCadastrar = document.getElementById('textoReivindicadoCadastrar');
    if (btnColetarCadastrar && !textoCadastrar) {
      textoCadastrar = document.createElement('span');
      textoCadastrar.id = 'textoReivindicadoCadastrar';
      textoCadastrar.style.color = 'green';
      textoCadastrar.style.marginLeft = '10px';
      btnColetarCadastrar.parentNode.appendChild(textoCadastrar);
    }

    // Assistir
    if (btnColetarAssistir) {
      if (status.jaReivindicouAssistirHoje) {
        btnColetarAssistir.style.display = 'none';
        textoAssistir.innerText = 'Você já reivindicou hoje.';
        textoAssistir.style.display = 'inline';
      } else if (assistirCount >= 20) {
        btnColetarAssistir.style.display = 'inline-block';
        textoAssistir.style.display = 'none';
      } else {
        btnColetarAssistir.style.display = 'none';
        textoAssistir.style.display = 'none';
      }
    }

    // Cadastrar
    if (btnColetarCadastrar) {
      if (status.jaReivindicouCadastrarHoje) {
        btnColetarCadastrar.style.display = 'none';
        textoCadastrar.innerText = 'Você já reivindicou hoje.';
        textoCadastrar.style.display = 'inline';
      } else if (cadastrarCount >= 1) {
        btnColetarCadastrar.style.display = 'inline-block';
        textoCadastrar.style.display = 'none';
      } else {
        btnColetarCadastrar.style.display = 'none';
        textoCadastrar.style.display = 'none';
      }
    }

    // MOSTRAR A SEÇÃO DE MISSÕES
    document.getElementById('secaoMissoes').style.display = 'block';
  }

  function carregarStatusMissoes() {
    const usuarioId = getUsuarioLogadoId();
    if (!usuarioId) {
      console.error('ID do usuário não encontrado.');
      return;
    }


	fetch(`/missoes/status/${usuarioId}`, {
	  credentials: 'include' // Aqui, dentro das opções do fetch
	})
      .then(res => {
		
        if (res.status === 404) {
          return { contadorAssistir: 0, contadorCadastrar: 0 };
        }
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then(status => {
        atualizarUIStatusMissoes(status);
      })
      .catch(err => {
        console.error('Erro ao carregar status das missões:', err);
        atualizarUIStatusMissoes({ contadorAssistir: 0, contadorCadastrar: 0 });
      });
  }

  
  // Função para reivindicar cadastro
  function reivindicarCadastro() {
    const usuarioId = getUsuarioLogadoId();
    if (!usuarioId) return;

    fetch(`/missoes/reivindicar-cadastro/${usuarioId}`, { method: 'POST' })
      .then(res => res.text())
      .then(msg => {
        alert(msg); // opcional manter o alert
        const btnColetarCadastrar = document.getElementById('btnColetarCadastrar');
        if (btnColetarCadastrar) {
          btnColetarCadastrar.innerText = msg; // muda texto do botão
          btnColetarCadastrar.disabled = true; // desabilita botão pra não clicar
          btnColetarCadastrar.style.display = 'inline-block'; // garante que está visível
        }
        carregarStatusMissoes();
      })
      .catch(console.error);
  }

  // Função para reivindicar assistir
  function reivindicarAssistir() {
    const usuarioId = getUsuarioLogadoId();
    if (!usuarioId) return;

    fetch(`/missoes/reivindicar-assistir/${usuarioId}`, { method: 'POST' })
      .then(res => res.text())
      .then(msg => {
        alert(msg); // opcional manter o alert
        const btnColetarAssistir = document.getElementById('btnColetarAssistir');
        if (btnColetarAssistir) {
          btnColetarAssistir.innerText = msg; // muda texto do botão
          btnColetarAssistir.disabled = true; // desabilita botão
          btnColetarAssistir.style.display = 'inline-block'; // garante visibilidade
        }
        carregarStatusMissoes();
      })
      .catch(console.error);
  }


  // Atualiza o status das missões a cada 3 segundos (opcional)
  setInterval(() => {
    const secaoMissoesVisivel = sections.missoes.style.display === 'block';
    if(secaoMissoesVisivel) {
      carregarStatusMissoes();
    }
  }, 3000);

});




