let saldoTokens = 0;
 let saldoUsdt = 0;

 document.addEventListener('DOMContentLoaded', () => {
   const fromToken = document.getElementById('fromToken');
   const toToken = document.getElementById('toToken');
   const fromAmount = document.getElementById('fromAmount');
   const toAmount = document.getElementById('toAmount');
   const preview = document.getElementById('previewSwap');
   const btnConfirmSwap = document.getElementById('btnConfirmSwap');
   const taxa = 0.02; // 2%

   function getUsuarioLogadoId() {
     const meta = document.querySelector('meta[name="user-id"]');
     return meta ? parseInt(meta.getAttribute('content')) : null;
   }

   function obterSaldo(token) {
     if (token === 'token') return saldoTokens;
     if (token === 'usdt') return saldoUsdt;
     return 0;
   }

   async function getCotacao(from, to) {
     if (from === 'token' && to === 'usdt') return 0.001;
     if (from === 'usdt' && to === 'token') return 1000;
     return 1;
   }

   function logMensagem(msg, cor = 'black') {
     const logEntry = document.createElement('div');
     logEntry.style.color = cor;
     logEntry.innerHTML = `📝 ${msg}`;
     preview.appendChild(logEntry);
   }

   async function atualizarResultado() {
     const from = fromToken.value;
     const to = toToken.value;
     const valor = parseFloat(fromAmount.value);

     preview.innerHTML = ''; // limpa os logs anteriores

     if (!valor || valor <= 0) {
       toAmount.value = '';
       return;
     }

     if (from === to) {
       toAmount.value = valor.toFixed(2);
       logMensagem('Selecione moedas diferentes para trocar.', 'red');
       return;
     }

     const saldoAtual = obterSaldo(from);
     if (valor > saldoAtual) {
       toAmount.value = '';
       logMensagem(`Saldo insuficiente! ${saldoAtual.toFixed(2)} ${from.toUpperCase()}.`, 'red');
       return;
     }

     if (from === 'token' && valor < 1000) {
       logMensagem('O valor mínimo para troca de ADZ Token é 1000.', 'red');
       toAmount.value = '';
       return;
     }

     if (from === 'usdt' && valor < 1) {
       logMensagem('O valor mínimo para troca de USDT é 1.', 'red');
       toAmount.value = '';
       return;
     }

     const cotacao = await getCotacao(from, to);
     const bruto = valor * cotacao;
     const taxaAplicada = bruto * taxa;
     const liquido = bruto - taxaAplicada;

     toAmount.value = liquido.toFixed(2);
     logMensagem(`Valor líquido: ${liquido.toFixed(2)} ${to.toUpperCase()}`, 'wite');
     
   }

   btnConfirmSwap.addEventListener('click', async () => {
     const valor = parseFloat(fromAmount.value);
     const de = fromToken.value;
     const para = toToken.value;

     if (!valor || valor <= 0) {
     //  alert("Digite um valor válido.");
       logMensagem('Digite um valor válido.');
       return;
     }

     if (de === para) {
      // alert("Selecione moedas diferentes.");
      logMensagem('Selecione moedas diferentes.');
       return;
     }

     const saldoAtual = obterSaldo(de);
     if (valor > saldoAtual) {
     //  alert(`Saldo insuficiente! ${saldoAtual.toFixed(2)} ${de.toUpperCase()}.`);
       return;
     }

     if (de === 'token' && valor < 1000) {
       alert("Valor mínimo para troca de ADZ Token é 1000.");
       return;
     }

     if (de === 'usdt' && valor < 1) {
       alert("Valor mínimo para troca de USDT é 1.");
       return;
     }

     try {
   	  const resultado = await realizarSwap(de, para, valor, getUsuarioLogadoId());

   	  if (resultado.sucesso) {
   	    if (de === 'token') {
   	      saldoTokens -= valor;
   	      saldoUsdt += resultado.valorRecebido;
   	    } else {
   	      saldoUsdt -= valor;
   	      saldoTokens += resultado.valorRecebido;
   	    }

   	    document.getElementById('saldoTokens').innerText = saldoTokens.toFixed(2);
   	    document.getElementById('saldoUsdt').innerText = saldoUsdt.toFixed(2);

   	    fromAmount.value = '';
   	    toAmount.value = '';
   	    preview.innerHTML = '';

   	    // Mostra mensagem de sucesso imediatamente
   	    logMensagem(`✅ Troca realizada: ${valor} ${de.toUpperCase()} → ${resultado.valorRecebido.toFixed(2)} ${para.toUpperCase()}`, 'wite');

   	    // Após 10 segundos, exibe "Realizar nova troca"
		
   	    setTimeout(() => {
   	     preview.innerHTML = '';
   	      logMensagem('🔄 Realizar nova troca?', 'wite'); 
   	    }, 10000); // 10 segundos = 10000 ms

   	  } else {
   	    logMensagem(`❌ Erro na troca: ${resultado.erro || 'Erro desconhecido'}`, 'red');
   	  }
   	} catch (err) {
   	  logMensagem(`❌ Erro ao realizar troca: ${err.message}`, 'red');
   	}

   });

   fromToken.addEventListener('change', atualizarResultado);
   toToken.addEventListener('change', atualizarResultado);
   fromAmount.addEventListener('input', atualizarResultado);

   atualizarResultado(); // Atualiza ao iniciar
 });

 function atualizarSaldoTokens() {
   return fetch('/api/saldo')
     .then(res => res.ok ? res.json() : Promise.reject("Erro na requisição saldo"))
     .then(data => {
       saldoTokens = data.saldo;
       document.getElementById('saldoTokens').innerText = saldoTokens.toFixed(2);
     })
     .catch(err => console.error(err));
 }

 function atualizarSaldoUsdt() {
   return fetch('/api/saldoUsdt')
     .then(res => res.ok ? res.json() : Promise.reject("Erro na requisição saldoUsdt"))
     .then(data => {
       saldoUsdt = data.saldoUsdt;
       document.getElementById('saldoUsdt').innerText = saldoUsdt.toFixed(2);
     })
     .catch(err => console.error(err));
 }

 async function realizarSwap(from, to, fromAmount, userId) {
   const url = '/api/swap';
   const swapRequest = {
     from: from,
     to: to,
     fromAmount: fromAmount,
     userId: userId
   };

   const response = await fetch(url, {
     method: 'POST',
     headers: { 'Content-Type': 'application/json' },
     body: JSON.stringify(swapRequest)
   });

   if (!response.ok) {
     const errorData = await response.json();
     return { sucesso: false, erro: errorData.erro || 'Erro desconhecido' };
   }

   const data = await response.json();
   return data;
 }

 atualizarSaldoTokens();
 atualizarSaldoUsdt();
 
 