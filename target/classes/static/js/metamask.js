// metamask.js

const connectButton = document.getElementById('connectButton');
const walletAddress = document.getElementById('walletAddress');
const status = document.getElementById('status');
let userAccount = null;
let provider;
let signer;

async function connectMetaMask() {
  if (typeof window.ethereum === "undefined") {
    status.innerText = "⚠️ MetaMask não encontrada. Instale a extensão no navegador.";
	Swal.fire({
	  icon: 'warning',
	  title: '⚠️ MetaMask não encontrada!',
	  text: 'Instale a extensão MetaMask no seu navegador para continuar.',
	  confirmButtonText: 'Entendi',
	  background: '#fff',
	  color: '#000'
	});

    return;
  }

  try {
    provider = new ethers.providers.Web3Provider(window.ethereum);
    await provider.send("eth_requestAccounts", []);
    signer = provider.getSigner();
    userAccount = await signer.getAddress();

    const network = await provider.getNetwork();
    if (network.chainId !== 56) {
      status.innerText = "⚠️ Rede incorreta. Por favor, conecte-se à Binance Smart Chain.";
	  alert('⚠️ MetaMask não encontrada. Instale a extensão no navegador.');
      return;
    }

    walletAddress.innerText = "Carteira: " + userAccount;
    status.innerText = "✅ MetaMask conectada com sucesso.";
	Swal.fire({
	  icon: 'success',
	  title: '✅ MetaMask Conectada!',
	  text: 'MetaMask conectada com sucesso.',
	  timer: 2500,
	  timerProgressBar: true,
	  showConfirmButton: false,
	  background: '#fff',
	  color: '#000'
	});

  } catch (error) {
    status.innerText = "❌ Erro ao conectar MetaMask: " + error.message;
	Swal.fire({
	  icon: 'error',
	  title: '❌ Erro ao conectar MetaMask',
	  text: `Erro: ${error.message}`,
	  confirmButtonText: 'Tentar novamente',
	  background: '#fff',
	  color: '#000'
	});

  }
}

connectButton.addEventListener('click', connectMetaMask);
