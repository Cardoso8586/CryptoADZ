const btnAnunciar     = document.getElementById('btnAnunciar');
const sectionAnunciar = document.getElementById('Anunciar');
const btnMostrarForm  = document.getElementById('btnMostrarForm');
const formAnuncio     = document.getElementById('formAnuncio');
const feedback        = document.getElementById('feedback');
const btnCancelar     = document.getElementById('btnCancelar');
const tempoInput      = document.getElementById('tempoVisualizacao');
const maxVisInput     = document.getElementById('maxVisualizacoes');
const infoPagamento   = document.getElementById('infoPagamento');
const feedbackCadastro  = document.getElementById('feedbackCadastro');
const saldoTokensElement = document.getElementById('saldoTokens'); // elemento que exibe o saldo

let custoEstimado = 0;
let saldoAtual = 0;

// Função para buscar saldo do usuário logado
async function buscarSaldo() {
  try {
    const res = await fetch('/api/saldo');
    if (!res.ok) throw new Error('Erro ao buscar saldo');
    const data = await res.json();
    saldoAtual = Number(data.saldo) || 0;
    if (saldoTokensElement) {
      saldoTokensElement.textContent = `Saldo atual: ${saldoAtual.toFixed(2)} tokens`;
    }
    return saldoAtual;
  } catch (err) {
    console.error(err);
    if (saldoTokensElement) {
      saldoTokensElement.textContent = 'Não foi possível obter saldo';
    }
    return 0;
  }
}

// Mostrar/ocultar a seção de anúncio
btnAnunciar.addEventListener('click', async e => {
  e.preventDefault();
  const vis = sectionAnunciar.style.display === 'none' ? 'block' : 'none';
  sectionAnunciar.style.display = vis;
  esconderFormulario();
  limparFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
  await buscarSaldo(); // atualiza saldo ao abrir
});

// Mostrar formulário de anúncio
btnMostrarForm.addEventListener('click', async () => {
  limparFormulario();
  mostrarFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
  await buscarSaldo(); // garante saldo atualizado
});

// Cancelar formulário
btnCancelar.addEventListener('click', () => {
  esconderFormulario();
  limparFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
});

function mostrarFormulario() {
  formAnuncio.style.display = 'block';
}

function esconderFormulario() {
  formAnuncio.style.display = 'none';
}

function limparFormulario() {
  formAnuncio.reset();
}

tempoInput.addEventListener('input', () => { calcularCustoEstimado(); buscarSaldo(); });
maxVisInput.addEventListener('input', () => { calcularCustoEstimado(); buscarSaldo(); });

function calcularCustoEstimado() {
  const tempo = parseInt(tempoInput.value, 10);
  const maxVis = parseInt(maxVisInput.value, 10);

  if (isNaN(tempo) || isNaN(maxVis) || tempo <= 0 || maxVis <= 0) {
    infoPagamento.textContent = '';
    custoEstimado = 0;
    return;
  }

  let tokensPorView;

  if (tempo >= 45) {
    tokensPorView = 4.50;
  } else if (tempo === 30) {
    tokensPorView = 3.55;
  } else if (tempo === 20) {
    tokensPorView = 2.25;
  } else if (tempo === 10) {
    tokensPorView = 1.10;
  } else {
    tokensPorView = 1.30;
  }

  custoEstimado = tokensPorView * maxVis;
  infoPagamento.textContent = `💰 Custo Estimado: ${custoEstimado.toFixed(2)} tokens`;
}

formAnuncio.addEventListener('submit', async e => {
  e.preventDefault();

  // ✅ Checa saldo antes de tudo
  const saldo = await buscarSaldo();
  if (saldo < custoEstimado) {
    Swal.fire({
      icon: 'warning',
      title: 'Saldo insuficiente',
      text: `Você precisa de ${custoEstimado.toFixed(2)} tokens, mas só tem ${saldo.toFixed(2)} tokens.`,
      confirmButtonText: 'Ok',
      background: '#fff',
      color: '#000'
    });
    return; // 🚫 Não envia
  }

  // ✅ Checa se o checkbox foi marcado
  const checkboxTermos = document.getElementById('aceitoTermosAnuncio');
  if (!checkboxTermos || !checkboxTermos.checked) {
    Swal.fire({
      icon: 'warning',
      title: 'Atenção',
      text: 'Você precisa aceitar os termos de privacidade para continuar.',
      confirmButtonText: 'Ok',
      background: '#fff',
      color: '#000'
    });
    feedback.textContent = '⛔ Você deve aceitar os Termos de Uso para continuar.';
    feedback.style.color = 'red';
    checkboxTermos?.focus();
    return;
  }

  const tempo = parseInt(tempoInput.value, 10);
  const maxVis = parseInt(maxVisInput.value, 10);

  if (isNaN(tempo) || isNaN(maxVis) || tempo <= 0 || maxVis <= 0) {
    feedback.textContent = '⛔ Tempo e visualizações devem ser válidos.';
    feedback.style.color = 'red';
    return;
  }

  const limitesMinimos = { 10: 300, 20: 500, 30: 700, 45: 1000 };
  const minimoExigido = limitesMinimos[tempo];
  if (minimoExigido && maxVis < minimoExigido) {
    Swal.fire({
      icon: 'warning',
      title: 'Aviso!',
      text: `⚠️ O número mínimo de visualizações para ${tempo} segundos é ${minimoExigido}.`,
      confirmButtonText: 'Entendi',
      background: '#fff',
      color: '#000'
    });
    feedback.textContent = `⚠️ O número mínimo de visualizações para ${tempo} segundos é ${minimoExigido}.`;
    feedback.style.color = 'red';
    maxVisInput.focus();
    return;
  }

  const dto = {
    titulo: document.getElementById('titulo').value,
    descricao: document.getElementById('descricao').value,
    url: document.getElementById('url').value,
    tempoVisualizacao: tempo,
    maxVisualizacoes: maxVis,
    usuarioId: 1,
    tokensGastos: Number(custoEstimado)
  };

  try {
    const response = await fetch('/api/anuncio/cadastrar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dto)
    });

    if (!response.ok) {
      const err = await response.text();
      if (err.toLowerCase().includes('saldo insuficiente')) {
        Swal.fire({
          icon: 'warning',
          title: 'Aviso!',
          text: `⛔ Saldo insuficiente para cadastrar o anúncio. Por favor, recarregue sua conta.`,
          confirmButtonText: 'Entendi',
          background: '#fff',
          color: '#000'
        });
		} else {
			Swal.fire({
			  icon: 'warning',
			  title: 'Ops!',
			  text: `🚫 Detectamos caracteres especiais que não são permitidos. 
			Digite manualmente ou use apenas letras, números e símbolos comuns.`,
			  confirmButtonText: 'Entendi!',
			  background: '#fff',
			  color: '#000'
			});


		    }
      feedback.style.color = 'red';
      return;
    }

    Swal.fire({
      icon: 'success',
      title: 'Sucesso',
      text: '✅ Seu anúncio foi cadastrado e está em validação.',
      timer: 6000,
      timerProgressBar: true,
      showConfirmButton: false,
      background: '#fff',
      color: '#000'
    });

    feedbackCadastro.textContent = '✅ Anúncio cadastrado com sucesso!';
    feedbackCadastro.style.color = 'green';
    esconderFormulario();
    limparFormulario();
    registrarMissaoCadastrar();
    infoPagamento.textContent = '';
    await buscarSaldo();

    setInterval(() => {
      carregarQuantidadeDeAnuncios();
    }, 3000);

  } catch (error) {
    feedback.textContent = 'Falha ao salvar o anúncio: Atualize essa página.';
    feedback.style.color = 'red';
  }
});
//====================
function registrarMissaoCadastrar() {
  const usuarioId = document.querySelector('meta[name="user-id"]')?.content;
  if (!usuarioId) {
    console.error('Usuário não encontrado para missão de cadastro.');
    return;
  }

  fetch(`/api/missoes/incrementar-cadastro/${usuarioId}`, { method: 'POST' })
    .then(res => {
      if (!res.ok) throw new Error('Falha ao registrar missão de cadastro');
      return res.text();
    })
    .then(msg => {
      console.log('Missão de cadastro registrada:', msg);
      return fetch(`/api/missoes/incrementar-cadastro/${usuarioId}`, { method: 'POST' });
    })
    .then(res => {
      if (!res.ok) throw new Error('Falha ao incrementar cadastro');
      console.log('Incremento de cadastro realizado com sucesso.');
      carregarStatusMissoes();
    })
    .catch(err => {
      console.error('Erro ao registrar/incrementar missão de cadastro:');
    });
}

const descricaoInput = document.getElementById('descricao');
const contadorDescricao = document.getElementById('contadorDescricao');
const spanRestantes = document.getElementById('restantes');
const limiteDescricao = 200;

descricaoInput.addEventListener('input', () => {
  const restante = limiteDescricao - descricaoInput.value.length;

  if (restante < 0) {
    descricaoInput.value = descricaoInput.value.substring(0, limiteDescricao);
    spanRestantes.textContent = '0';
  } else {
    spanRestantes.textContent = restante;
  }

  if (restante <= 20 && restante >= 0) {
    spanRestantes.style.color = 'orange';
  } else if (restante === 0) {
    spanRestantes.style.color = 'red';
  } else {
    spanRestantes.style.color = 'gray';
  }
});

const tituloInput = document.getElementById('titulo');
const restantesTitulo = document.getElementById('restantesTitulo');

tituloInput.addEventListener('input', () => {
  const max = 30;
  const atual = tituloInput.value.length;
  const restantes = max - atual;

  restantesTitulo.textContent = restantes;

  if (restantes <= 0) {
    restantesTitulo.style.color = 'red';
  } else {
    restantesTitulo.style.color = 'gray';
  }
});
