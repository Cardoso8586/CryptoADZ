const tabKey = 'app-tab-open';
const blockKey = 'app-tab-blocked';

let isMainTab = false;

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

function tryRegisterTab() {
  const currentTab = localStorage.getItem(tabKey);
  if (!currentTab) {
    // Se não tiver nenhuma aba registrada, registre esta como principal
    localStorage.setItem(tabKey, Date.now().toString());
    isMainTab = true;
    localStorage.removeItem(blockKey);
    return true;
  } else {
    return false;
  }
}

function clearTab() {
  if (isMainTab) {
    localStorage.removeItem(tabKey);
    localStorage.removeItem(blockKey);
  }
}

window.addEventListener('storage', (e) => {
  if (e.key === tabKey) {
    // Quando a aba principal fecha, a chave é removida
    if (!localStorage.getItem(tabKey)) {
      // Tente registrar esta aba como principal (se não for principal)
      if (!isMainTab && tryRegisterTab()) {
        // Se conseguiu registrar, limpa bloqueio e recarrega para liberar acesso
        localStorage.removeItem(blockKey);
        location.reload();
      }
    } else {
      // Se outra aba principal foi registrada e esta não é, bloqueie-se
      if (!isMainTab) {
        localStorage.setItem(blockKey, 'true');
        showBlockMessage();
      }
    }
  }

  if (e.key === blockKey) {
    // Se bloqueio for removido, significa que aba pode liberar acesso
    if (!localStorage.getItem(blockKey) && !isMainTab) {
      location.reload();
    }
  }
});

if (tryRegisterTab()) {
  // Esta aba é principal
  blockNavigationKeys();
  blockHistoryNavigation();

  window.addEventListener('beforeunload', () => {
    clearTab();
  });
} else {
  // Outra aba já está aberta, bloqueie esta
  localStorage.setItem(blockKey, 'true');
  showBlockMessage();
}

function blockNavigationKeys() {
  window.addEventListener('keydown', function(e) {
    if (['Backspace', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
      e.preventDefault();
    }
  });
}

function blockHistoryNavigation() {
  history.pushState(null, document.title, location.href);
  window.addEventListener('popstate', function () {
    history.pushState(null, document.title, location.href);
  });
}

