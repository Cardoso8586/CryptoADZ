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
  const now = Date.now();

  if (!currentTab || now - parseInt(currentTab, 10) > 10000) { // 10 segundos
    localStorage.setItem(tabKey, now.toString());
    isMainTab = true;
    localStorage.removeItem(blockKey);
    return true;
  }
  return false;
}

function clearTab() {
  if (isMainTab) {
    localStorage.removeItem(tabKey);
    localStorage.removeItem(blockKey);
  }
}

window.addEventListener('storage', (e) => {
  if (e.key === tabKey) {
    if (!localStorage.getItem(tabKey)) {
      if (!isMainTab && tryRegisterTab()) {
        localStorage.removeItem(blockKey);
        location.reload();
      }
    } else {
      if (!isMainTab) {
        localStorage.setItem(blockKey, 'true');
        showBlockMessage();
      }
    }
  }

  if (e.key === blockKey) {
    if (!localStorage.getItem(blockKey) && !isMainTab) {
      location.reload();
    }
  }
});

if (tryRegisterTab()) {
  setInterval(() => {
    if (isMainTab) {
      localStorage.setItem(tabKey, Date.now().toString());
    }
  }, 5000);
} else {
  localStorage.setItem(blockKey, 'true');
  showBlockMessage();
}

// Impede uso de teclas que causam navegação
function blockNavigationKeys() {
  window.addEventListener('keydown', (e) => {
    if (['Backspace', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
      e.preventDefault();
    }
  });
}

// Impede navegação pelo botão "Voltar"
function blockHistoryNavigation() {
  history.pushState(null, document.title, location.href);
  window.addEventListener('popstate', () => {
    history.pushState(null, document.title, location.href);
  });
}

// Chamada das proteções de navegação
blockNavigationKeys();
blockHistoryNavigation();

// Limpa chave ao sair
window.addEventListener('beforeunload', clearTab);


