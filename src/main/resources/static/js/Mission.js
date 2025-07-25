
  const sections = {
    verAnuncios: document.getElementById('VerAnuncios'),
    anunciar:    document.getElementById('Anunciar'),
    wallet:      document.getElementById('Wallet'),
    missoes:     document.getElementById('secaoMissoes')
  };

  function hideAll() {
    Object.values(sections).forEach(sec => {
      if (sec) {
        sec.style.visibility = 'hidden';   // invisível, mas ocupa espaço
        sec.style.position = 'absolute';   // tira do fluxo visual
        sec.style.left = '-9999px';        // fora da tela
        sec.style.top = '0';
        sec.style.display = 'block';       // manter display para scripts
      }
    });
  }

  function show(name) {
    hideAll();
    if (sections[name]) {
      sections[name].style.visibility = 'visible';
      sections[name].style.position = 'static';
      sections[name].style.left = 'auto';
      sections[name].style.display = 'block';
    }
  }


  document.getElementById('btnVerAnuncios')?.addEventListener('click', e => { e.preventDefault(); show('verAnuncios'); });
  document.getElementById('btnAnunciar')?.addEventListener('click', e => { e.preventDefault(); show('anunciar'); });
  document.getElementById('btnWallet')?.addEventListener('click', e => { e.preventDefault(); show('wallet'); });
  document.getElementById('btnMissoes')?.addEventListener('click', e => {
    e.preventDefault();
    show('missoes');
    carregarStatusMissoes();
  });

  // Função para obter ID do usuário logado
  function getUsuarioLogadoId() {
    const meta = document.querySelector('meta[name="user-id"]');
    return meta ? meta.getAttribute('content') : null;
  }

  // Função para reivindicar cadastro
  function reivindicarCadastro() {
    const usuarioId = getUsuarioLogadoId();
    if (!usuarioId) return;

    fetch(`/missoes/reivindicar-cadastro/${usuarioId}`, { method: 'POST' })
      .then(res => res.text())
      .then(msg => {
        alert(msg);
        const btn = document.getElementById('btnColetarCadastrar');
        if (btn) {
          btn.innerText = msg;
          btn.disabled = true;
          btn.style.display = 'inline-block';
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
        alert(msg);
        const btn = document.getElementById('btnColetarAssistir');
        if (btn) {
          btn.innerText = msg;
          btn.disabled = true;
          btn.style.display = 'inline-block';
        }
        carregarStatusMissoes();
      })
      .catch(console.error);
  }

  // Eventos para os botões de reivindicar
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

  function atualizarUIStatusMissoes(status) {
    console.log('Status recebido:', status);

    const assistirCount  = status.contadorAssistir ?? 0;
    const cadastrarCount = status.contadorCadastrar ?? 0;

    const statusAssistir = document.getElementById('statusAssistir');
    const statusCadastro = document.getElementById('statusCadastro');
    if (statusAssistir) statusAssistir.innerText = assistirCount >= 20 ? '✅' : `${assistirCount}/20`;
    if (statusCadastro) statusCadastro.innerText = cadastrarCount >= 1 ? '✅' : `${cadastrarCount}/1`;

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

    // Mostrar missão
    document.getElementById('secaoMissoes').style.display = 'block';
  }

  function carregarStatusMissoes() {
    const usuarioId = getUsuarioLogadoId();
    if (!usuarioId) {
      console.error('ID do usuário não encontrado.');
      return;
    }

    fetch(`/missoes/status/${usuarioId}`, { credentials: 'include' })
      .then(res => {
        if (res.status === 404) {
          return { contadorAssistir: 0, contadorCadastrar: 0, recompensa_Assistir: 0, recompensa_Registrar: 0 };
        }
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then(status => {
        atualizarUIStatusMissoes(status);

        const recompensaMissaorAssistir = document.getElementById('recompensaMissaorAssistir');
        if (recompensaMissaorAssistir && status.recompensa_Assistir != null) {
          recompensaMissaorAssistir.innerHTML = `${status.recompensa_Assistir} <img src="/icones/adz-token.png" alt="ADZ Token">`;
        }

        const recompensaMissaorRegister = document.getElementById('recompensaMissaorRegister');
        if (recompensaMissaorRegister && status.recompensa_Registrar != null) {
          recompensaMissaorRegister.innerHTML = `${status.recompensa_Registrar} <img src="/icones/adz-token.png" alt="ADZ Token">`;
        }
      })
      .catch(err => {
        console.error('Erro ao carregar status das missões:', err);
        atualizarUIStatusMissoes({ contadorAssistir: 0, contadorCadastrar: 0 });

        const recompensaMissaorAssistir = document.getElementById('recompensaMissaorAssistir');
        if (recompensaMissaorAssistir) {
          recompensaMissaorAssistir.innerHTML = `recompensaMissaorAssistir">`;
        }
      });
  }

  // Mostra a primeira seção por padrão
  show('verAnuncios');

  // Atualiza missões a cada 3 segundos se estiver visível
  setInterval(() => {
    const secaoMissoesVisivel = sections.missoes.style.display === 'block' || sections.missoes.style.visibility === 'visible';
    if (secaoMissoesVisivel) {
      carregarStatusMissoes();
    }
  }, 30000);




