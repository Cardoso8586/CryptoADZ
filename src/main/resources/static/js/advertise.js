const btnAnunciar     = document.getElementById('btnAnunciar');
const sectionAnunciar = document.getElementById('Anunciar');
const btnMostrarForm  = document.getElementById('btnMostrarForm');
const formAnuncio     = document.getElementById('formAnuncio');
const feedback        = document.getElementById('feedback');
const btnCancelar     = document.getElementById('btnCancelar');
const tempoInput      = document.getElementById('tempoVisualizacao');
const maxVisInput     = document.getElementById('maxVisualizacoes');
const infoPagamento   = document.getElementById('infoPagamento');

// Mostrar/ocultar a seção de anúncio
btnAnunciar.addEventListener('click', e => {
  e.preventDefault();
  const vis = sectionAnunciar.style.display === 'none' ? 'block' : 'none';
  sectionAnunciar.style.display = vis;
  esconderFormulario();
  limparFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
});

// Mostrar formulário de anúncio
btnMostrarForm.addEventListener('click', () => {
  limparFormulario();
  mostrarFormulario();
  feedback.textContent = '';
  infoPagamento.textContent = '';
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

// Cálculo automático do custo do anúncio
tempoInput.addEventListener('input', calcularCustoEstimado);
maxVisInput.addEventListener('input', calcularCustoEstimado);

let custoEstimado = 0;

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
    tokensPorView = 1.30; // valor padrão se tempo for algo inesperado
  }

  custoEstimado = tokensPorView * maxVis;

  infoPagamento.textContent = `💰 Custo Estimado:  ${custoEstimado.toFixed(2)} tokens`;
}

formAnuncio.addEventListener('submit', async e => {
  e.preventDefault();

  const tempo = parseInt(tempoInput.value, 10);
  const maxVis = parseInt(maxVisInput.value, 10);

  if (isNaN(tempo) || isNaN(maxVis) || tempo <= 0 || maxVis <= 0) {
    feedback.textContent = '⛔ Tempo e visualizações devem ser válidos.';
    feedback.style.color = 'red';
    return;
  }

  // Validação de mínimos por tempo
  const limitesMinimos = {
    10: 300,
    20: 500,
    30: 700,
    45: 1000,
  };

  const minimoExigido = limitesMinimos[tempo];

  if (minimoExigido && maxVis < minimoExigido) {
    feedback.textContent = `⚠️ O número mínimo de visualizações para ${tempo} segundos é ${minimoExigido}.`;
    feedback.style.color = 'red';
    maxVisInput.focus();
    return;
  }

  // DTO do anúncio
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

      // Ajuste: tratar erro específico de saldo insuficiente
      if (err.toLowerCase().includes('saldo insuficiente')) {
        feedback.textContent = '⛔ Saldo insuficiente para cadastrar o anúncio. Por favor, recarregue sua conta.';
      } else {
        feedback.textContent = 'Erro: ' + err;
      }
      
      feedback.style.color = 'red';
      return;
    }

    const data = await response.json();
    console.log('Server response:', data);

    feedback.textContent = '✅ Anúncio cadastrado com sucesso!';
    feedback.style.color = 'green';
    esconderFormulario();
    limparFormulario();
    registrarMissaoCadastrar();
    infoPagamento.textContent = '';
    atualizarSaldo();
    
    setInterval(() => {
      carregarQuantidadeDeAnuncios();
    }, 3000); // Atualiza a cada 3 segundos

  } catch (error) {
    feedback.textContent = 'Falha ao salvar o anúncio: ' + error.message;
    feedback.style.color = 'red';
  }
});


function registrarMissaoCadastrar() {
  const usuarioId = document.querySelector('meta[name="user-id"]')?.content;
  if (!usuarioId) {
    console.error('Usuário não encontrado para missão de cadastro.');
    return;
  }

  fetch(`/missoes/incrementar-cadastro/${usuarioId}`, { method: 'POST' })
    .then(res => {
      if (!res.ok) throw new Error('Falha ao registrar missão de cadastro');
      return res.text();
    })
    .then(msg => {
      console.log('Missão de cadastro registrada:', msg);
      // Agora incrementa
      return fetch(`/missoes/incrementar-cadastro/${usuarioId}`, { method: 'POST' });
    })
    .then(res => {
      if (!res.ok) throw new Error('Falha ao incrementar cadastro');
      console.log('Incremento de cadastro realizado com sucesso.');
      carregarStatusMissoes(); // Atualiza os contadores na tela
    })
    .catch(err => {
      console.error('Erro ao registrar/incrementar missão de cadastro:', err);
    });
}



