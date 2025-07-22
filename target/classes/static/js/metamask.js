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
      return;
    }

    walletAddress.innerText = "Carteira: " + userAccount;
    status.innerText = "✅ MetaMask conectada com sucesso.";
  } catch (error) {
    status.innerText = "❌ Erro ao conectar MetaMask: " + error.message;
  }
}

connectButton.addEventListener('click', connectMetaMask);
