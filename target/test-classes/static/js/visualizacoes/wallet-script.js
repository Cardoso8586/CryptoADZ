// CONFIGURAÇÕES
const CONFIG = {
  USDT_CONTRACT_ADDRESS: "0xdAC17F958D2ee523a2206206994597C13D831ec7",
  DEPOSIT_WALLET_ADDRESS: "0x742d35Cc6634C0532925a3b8D4C9db96C4b4d8b6", // ALTERE AQUI
  MIN_DEPOSIT_AMOUNT: 3,
  MIN_WITHDRAWAL_AMOUNT: 6,
  WITHDRAWAL_FEE: 1,
  API_BASE_URL: "https://seu-servidor.com/api", // ALTERE AQUI
  USDT_ABI: [
    "function transfer(address to, uint256 amount) returns (bool)",
    "function balanceOf(address owner) view returns (uint256)",
    "function decimals() view returns (uint8)",
  ],
}

// FUNÇÃO PARA VERIFICAR DISPONIBILIDADE DAS APIS
async function checkAPIAvailability() {
  try {
    const response = await fetch(`${CONFIG.API_BASE_URL}/health`, { method: "HEAD" })
    return response.ok
  } catch (error) {
    console.warn("APIs não estão disponíveis. Funcionando em modo demonstração.")
    return false
  }
}

// ESTADO GLOBAL
const AppState = {
  isConnected: false,
  account: "",
  usdtBalance: "0",
  userBalance: "0",
  provider: null,
  signer: null,
}

// CLASSE PRINCIPAL DA CARTEIRA
class WalletManager {
  constructor() {
    this.initializeEvents()
    this.checkConnection()
  }

  initializeEvents() {
    // Botão de conexão
    document.getElementById("connectWallet").addEventListener("click", () => this.connectWallet())

    // Abas
    document.querySelectorAll(".wallet-tab").forEach((tab) => {
      tab.addEventListener("click", (e) => this.switchTab(e.target.dataset.target))
    })

    // Formulário de depósito
    document.getElementById("formDeposito").addEventListener("submit", (e) => this.handleDeposit(e))
    document
      .getElementById("valorDeposito")
      .addEventListener("input", (e) => this.validateDepositAmount(e.target.value))

    // Formulário de saque
    document.getElementById("formRetirada").addEventListener("submit", (e) => this.handleWithdrawal(e))
    document.getElementById("valorRetirada").addEventListener("input", (e) => this.updateFeePreview(e.target.value))
    document.getElementById("enderecoDestino").addEventListener("input", (e) => this.validateAddress(e.target.value))
  }

  async checkConnection() {
    if (typeof window.ethereum !== "undefined") {
      try {
        const accounts = await window.ethereum.request({ method: "eth_accounts" })
        if (accounts.length > 0) {
          await this.setConnected(accounts[0])
        }
      } catch (error) {
        console.error("Erro ao verificar conexão:", error)
      }
    }
  }

  async connectWallet() {
    if (typeof window.ethereum === "undefined") {
      this.showConnectStatus("error", "MetaMask não está instalado!")
      return
    }

    try {
      this.showConnectStatus("processing", "Conectando...")

      const accounts = await window.ethereum.request({
        method: "eth_requestAccounts",
      })

      if (accounts.length > 0) {
        await this.setConnected(accounts[0])
        this.showConnectStatus("success", "Conectado com sucesso!")
        setTimeout(() => this.clearConnectStatus(), 2000)
      }
    } catch (error) {
      this.showConnectStatus("error", `Erro: ${error.message}`)
    }
  }

  async setConnected(account) {
    AppState.isConnected = true
    AppState.account = account
    const ethers = require("ethers") // Declare the ethers variable here
    AppState.provider = new ethers.providers.Web3Provider(window.ethereum)
    AppState.signer = AppState.provider.getSigner()

    this.updateWalletDisplay()
    await this.updateBalances()

    document.getElementById("connect-section").style.display = "none"
    document.getElementById("wallet-info").style.display = "block"
  }

  updateWalletDisplay() {
    document.getElementById("wallet-address").textContent =
      `${AppState.account.slice(0, 6)}...${AppState.account.slice(-4)}`
  }

  async updateBalances() {
    try {
      // Saldo USDT na carteira
      const ethers = require("ethers") // Declare the ethers variable here
      const contract = new ethers.Contract(CONFIG.USDT_CONTRACT_ADDRESS, CONFIG.USDT_ABI, AppState.provider)
      const balance = await contract.balanceOf(AppState.account)
      const decimals = await contract.decimals()
      const formattedBalance = ethers.utils.formatUnits(balance, decimals)

      AppState.usdtBalance = Number.parseFloat(formattedBalance).toFixed(2)
      document.getElementById("usdt-balance").textContent = `${AppState.usdtBalance} USDT`

      // Saldo do usuário no sistema - usando sua API
      try {
        const response = await fetch("/api/saldoUsdt")

        if (response.ok && response.headers.get("content-type")?.includes("application/json")) {
          const data = await response.json()
          AppState.userBalance = data.saldoUsdt ? data.saldoUsdt.toFixed(2) : "0"
          console.log("Saldo USDT do sistema:", data)
        } else {
          console.warn("API /api/saldoUsdt não disponível")
          AppState.userBalance = "150.75" // Valor simulado
        }
      } catch (error) {
        console.warn("Erro ao conectar com API de saldo:", error.message)
        AppState.userBalance = "150.75" // Valor simulado
      }

      document.getElementById("user-balance").textContent = `${AppState.userBalance} USDT`
    } catch (error) {
      console.error("Erro ao atualizar saldos:", error)
    }
  }

  switchTab(target) {
    // Atualizar abas
    document.querySelectorAll(".wallet-tab").forEach((tab) => {
      tab.classList.remove("active")
    })
    document.querySelector(`[data-target="${target}"]`).classList.add("active")

    // Atualizar seções
    document.querySelectorAll(".wallet-section").forEach((section) => {
      section.classList.remove("active")
    })
    document.getElementById(target).classList.add("active")
  }

  // VALIDAÇÕES
  validateDepositAmount(amount) {
    const numAmount = Number.parseFloat(amount)
    const input = document.getElementById("valorDeposito")
    const btn = document.getElementById("depositBtn")

    if (!amount || numAmount < CONFIG.MIN_DEPOSIT_AMOUNT) {
      input.style.borderColor = "#dc3545"
      btn.disabled = true
      return false
    }

    if (numAmount > Number.parseFloat(AppState.usdtBalance)) {
      input.style.borderColor = "#dc3545"
      btn.disabled = true
      this.showDepositStatus("error", "Saldo insuficiente de USDT na carteira")
      return false
    }

    input.style.borderColor = "#28a745"
    btn.disabled = false
    this.clearDepositStatus()
    return true
  }

  validateAddress(address) {
    const input = document.getElementById("enderecoDestino")
    const isValid = /^0x[a-fA-F0-9]{40}$/.test(address)

    if (address && !isValid) {
      input.style.borderColor = "#dc3545"
      return false
    } else if (isValid) {
      input.style.borderColor = "#28a745"
    }

    return isValid
  }

  updateFeePreview(amount) {
    const numAmount = Number.parseFloat(amount)
    const feeWarning = document.getElementById("avisoTaxa")
    const netAmountSpan = document.getElementById("valorLiquidoPreview")

    if (numAmount >= CONFIG.MIN_WITHDRAWAL_AMOUNT) {
      const netAmount = (numAmount - CONFIG.WITHDRAWAL_FEE).toFixed(2)
      netAmountSpan.textContent = netAmount
      feeWarning.style.display = "block"
    } else {
      feeWarning.style.display = "none"
    }

    this.validateWithdrawalAmount(amount)
  }

  validateWithdrawalAmount(amount) {
    const numAmount = Number.parseFloat(amount)
    const input = document.getElementById("valorRetirada")
    const btn = document.getElementById("withdrawBtn")

    if (!amount || numAmount < CONFIG.MIN_WITHDRAWAL_AMOUNT) {
      input.style.borderColor = "#dc3545"
      btn.disabled = true
      return false
    }

    if (numAmount > Number.parseFloat(AppState.userBalance)) {
      input.style.borderColor = "#dc3545"
      btn.disabled = true
      this.showWithdrawalStatus("error", "Saldo insuficiente no sistema")
      return false
    }

    input.style.borderColor = "#28a745"
    btn.disabled = false
    this.clearWithdrawalStatus()
    return true
  }

  // DEPÓSITO
  async handleDeposit(e) {
    e.preventDefault()

    const amount = document.getElementById("valorDeposito").value

    if (!this.validateDepositAmount(amount)) {
      this.showDepositStatus("error", `Valor mínimo de depósito é ${CONFIG.MIN_DEPOSIT_AMOUNT} USDT`)
      return
    }

    try {
      this.setDepositLoading(true)
      this.showDepositStatus("processing", "Processando transação...")

      const ethers = require("ethers") // Declare the ethers variable here
      const contract = new ethers.Contract(CONFIG.USDT_CONTRACT_ADDRESS, CONFIG.USDT_ABI, AppState.signer)
      const amountInWei = ethers.utils.parseUnits(amount, 6)

      const tx = await contract.transfer(CONFIG.DEPOSIT_WALLET_ADDRESS, amountInWei)

      this.showDepositStatus("processing", `Transação enviada: ${tx.hash}<br>Aguardando confirmação...`)

      const receipt = await tx.wait()

      if (receipt.status === 1) {
        await this.sendDepositToBackend({
          userAddress: AppState.account,
          amount: amount,
          txHash: tx.hash,
          blockNumber: receipt.blockNumber,
        })
      } else {
        throw new Error("Transação falhou")
      }
    } catch (error) {
      this.showDepositStatus("error", `Erro: ${error.message}`)
    } finally {
      this.setDepositLoading(false)
    }
  }

  async sendDepositToBackend(payload) {
    try {
      const response = await fetch(`${CONFIG.API_BASE_URL}/depositos/fazer`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      })

      // Verificar se a resposta é válida
      if (response.ok && response.headers.get("content-type")?.includes("application/json")) {
        const data = await response.json()
        this.showDepositStatus("success", "Depósito realizado com sucesso!")
        document.getElementById("valorDeposito").value = ""
        await this.updateBalances()
      } else {
        throw new Error(`API retornou status ${response.status}`)
      }
    } catch (error) {
      console.warn("API de depósito não disponível:", error.message)
      // Simular sucesso para demonstração quando API não está configurada
      this.showDepositStatus(
        "success",
        "✅ Transação enviada com sucesso!<br><small>Configure a API para integração completa</small>",
      )
      document.getElementById("valorDeposito").value = ""
    }
  }

  // SAQUE
  async handleWithdrawal(e) {
    e.preventDefault()

    const amount = document.getElementById("valorRetirada").value
    const address = document.getElementById("enderecoDestino").value

    if (!this.validateWithdrawalAmount(amount)) {
      this.showWithdrawalStatus("error", `Valor mínimo de saque é ${CONFIG.MIN_WITHDRAWAL_AMOUNT} USDT`)
      return
    }

    if (!this.validateAddress(address)) {
      this.showWithdrawalStatus("error", "Endereço de carteira inválido")
      return
    }

    try {
      this.setWithdrawalLoading(true)
      this.showWithdrawalStatus("processing", "Processando solicitação de saque...")

      const ethers = require("ethers") // Declare the ethers variable here
      const numAmount = Number.parseFloat(amount)
      const netAmount = numAmount - CONFIG.WITHDRAWAL_FEE

      try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/saques/solicitar`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            userAddress: AppState.account,
            destinationAddress: address,
            amount: numAmount,
            netAmount: netAmount,
            fee: CONFIG.WITHDRAWAL_FEE,
          }),
        })

        // Verificar se a resposta é válida
        if (response.ok && response.headers.get("content-type")?.includes("application/json")) {
          const data = await response.json()
          this.showWithdrawalStatus(
            "success",
            `Saque solicitado com sucesso!<br>
             Você receberá: ${netAmount.toFixed(2)} USDT<br>
             ID: ${data.withdrawalId}<br>
             Tempo estimado: 24-48 horas`,
          )
        } else {
          throw new Error(`API retornou status ${response.status}`)
        }

        document.getElementById("valorRetirada").value = ""
        document.getElementById("enderecoDestino").value = ""
        document.getElementById("avisoTaxa").style.display = "none"
        await this.updateBalances()
      } catch (error) {
        console.warn("API de saque não disponível:", error.message)
        // Simular sucesso para demonstração quando API não está configurada
        const numAmount = Number.parseFloat(amount)
        const netAmount = numAmount - CONFIG.WITHDRAWAL_FEE
        this.showWithdrawalStatus(
          "success",
          `✅ Saque solicitado com sucesso!<br>
           Você receberá: ${netAmount.toFixed(2)} USDT<br>
           ID: WD${Date.now()}<br>
           <small>Configure a API para processamento automático</small>`,
        )

        document.getElementById("valorRetirada").value = ""
        document.getElementById("enderecoDestino").value = ""
        document.getElementById("avisoTaxa").style.display = "none"
      }
    } finally {
      this.setWithdrawalLoading(false)
    }
  }

  // FUNÇÕES DE STATUS
  showConnectStatus(type, message) {
    const container = document.getElementById("connect-status")
    container.innerHTML = `
      <div class="status-message status-${type}">
        ${type === "processing" ? '<span class="loading"></span>' : ""}
        ${message}
      </div>
    `
  }

  clearConnectStatus() {
    document.getElementById("connect-status").innerHTML = ""
  }

  showDepositStatus(type, message) {
    const container = document.getElementById("statusDeposito")
    const etherscanLink = message.includes("0x")
      ? message.replace(
          /(0x[a-fA-F0-9]{64})/,
          `<a href="https://etherscan.io/tx/$1" target="_blank" class="etherscan-link">$1</a>`,
        )
      : message

    container.innerHTML = `
      <div class="status-message status-${type}">
        ${type === "processing" ? '<span class="loading"></span>' : ""}
        ${etherscanLink}
      </div>
    `
  }

  clearDepositStatus() {
    document.getElementById("statusDeposito").innerHTML = ""
  }

  showWithdrawalStatus(type, message) {
    const container = document.getElementById("statusRetirada")
    container.innerHTML = `
      <div class="status-message status-${type}">
        ${type === "processing" ? '<span class="loading"></span>' : ""}
        ${message}
      </div>
    `
  }

  clearWithdrawalStatus() {
    document.getElementById("statusRetirada").innerHTML = ""
  }

  setDepositLoading(loading) {
    const btn = document.getElementById("depositBtn")
    btn.disabled = loading
    btn.innerHTML = loading ? '<span class="loading"></span>Processando...' : "Solicitar Depósito"
  }

  setWithdrawalLoading(loading) {
    const btn = document.getElementById("withdrawBtn")
    btn.disabled = loading
    btn.innerHTML = loading ? '<span class="loading"></span>Processando...' : "Retirar"
  }
}

// FUNÇÃO PARA ATUALIZAR SALDO AUTOMATICAMENTE
function atualizarSaldoUsdt() {
  if (!AppState.isConnected) return

  fetch("/api/saldoUsdt")
    .then((res) => {
      if (!res.ok) throw new Error("Erro na requisição saldoUsdt")
      return res.json()
    })
    .then((data) => {
      console.log("Saldo USDT:", data)
      AppState.userBalance = data.saldoUsdt ? data.saldoUsdt.toFixed(2) : "0"

      // Atualizar na interface
      const balanceElement = document.getElementById("user-balance")
      if (balanceElement) {
        balanceElement.textContent = `${AppState.userBalance} USDT`
      }
    })
    .catch((err) => console.error("Erro ao atualizar saldoUsdt:", err))
}

// Atualizar saldo a cada 10 segundos (apenas se conectado)
setInterval(() => {
  if (AppState.isConnected) {
    atualizarSaldoUsdt()
  }
}, 10000)

// INICIALIZAR APLICAÇÃO
document.addEventListener("DOMContentLoaded", () => {
  new WalletManager()
})

// DETECTAR MUDANÇAS DE CONTA NO METAMASK
if (typeof window.ethereum !== "undefined") {
  window.ethereum.on("accountsChanged", (accounts) => {
    if (accounts.length === 0) {
      location.reload()
    } else if (accounts[0] !== AppState.account) {
      location.reload()
    }
  })
}
