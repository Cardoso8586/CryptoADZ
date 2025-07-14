const tabKey = 'app-tab-open';
const blockKey = 'app-tab-blocked';

let isMainTab = false;

// Mostrar mensagem de bloqueio
function showBlockMessage() {
  document.body.innerHTML = `
    <div style="margin: 50px auto; max-width: 600px; text-align: center; font-family: Arial, sans-serif;">
      <h2 style="color: red;">⚠️ Access Blocked</h2>
      <p style="font-size: 16px; color: #333;">
        This application is already open in another browser tab or window.<br>
        For security reasons, multiple sessions are not allowed.
      </p>
      <p style="font-size: 14px; color: #666; margin-top: 20px;">
        Please close this tab and continue using the original one.
      </p>
      <button id="btnClose" style="margin-top: 30px; padding: 10px 20px; background-color: #d9534f; color: white; border: none; border-radius: 5px; cursor: pointer;">
        Close this tab
      </button>
    </div>
  `;

  document.getElementById('btnClose').addEventListener('click', () => {
    window.open('', '_self');
    window.close();

    document.body.innerHTML = `
      <div style="margin: 50px auto; max-width: 500px; text-align: center; font-family: Arial, sans-serif;">
        <h2 style="color: red;">❌ Unable to close automatically.</h2>
        <p style="font-size: 15px;">Please close this tab manually.</p>
      </div>
    `;
  });
}

// Tenta registrar esta aba como a principal
function tryRegisterTab() {
  const currentTab = localStorage.getItem(tabKey);

  if (!currentTab) {
    localStorage.setItem(tabKey, Date.now().toString());
    isMainTab = true;
    localStorage.removeItem(blockKey);
    return true;
  } else {
    return false;
  }
}

// Limpa registro da aba principal ao fechar
function clearTab() {
  if (isMainTab) {
    localStorage.removeItem(tabKey);
    localStorage.removeItem(blockKey);
  }
}

// Escuta mudanças no localStorage (comunicação entre abas)
window.addEventListener('storage', (e) => {
  if (e.key === tabKey) {
    // Se aba principal foi removida, tenta registrar esta aba
    if (!localStorage.getItem(tabKey)) {
      if (tryRegisterTab()) {
        location.reload(); // recarrega para liberar o acesso
      }
    } else {
      if (!isMainTab) {
        localStorage.setItem(blockKey, 'true');
        showBlockMessage();
      }
    }
  }
});

// Bloqueia navegação do teclado
function blockNavigationKeys() {
  window.addEventListener('keydown', function(e) {
    if (['Backspace', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
      e.preventDefault();
    }
  });
}

// Bloqueia botões de voltar/avançar do navegador
function blockHistoryNavigation() {
  history.pushState(null, document.title, location.href);
  window.addEventListener('popstate', function () {
    history.pushState(null, document.title, location.href);
  });
}

// Verifica no carregamento se já existe aba principal
if (tryRegisterTab()) {
  // Esta aba é a principal
  blockNavigationKeys();
  blockHistoryNavigation();

  window.addEventListener('beforeunload', () => {
    clearTab();
  });
} else {
  // Outra aba já está aberta, bloqueia esta
  localStorage.setItem(blockKey, 'true');
  showBlockMessage();
}


