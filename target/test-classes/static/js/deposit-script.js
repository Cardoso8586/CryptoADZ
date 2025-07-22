// SCRIPT DE DEPÓSITO
const WALLET_CONFIG = {
    MIN_DEPOSIT_AMOUNT: 10,
    USDT_CONTRACT_ADDRESS: '0x...',\
    USDT_ABI: [...],
    API_ENDPOINTS: {
        DEPOSIT: 'https://api.example.com/deposit'
    },
    DEPOSIT_WALLET_ADDRESS: '0x...' // Added missing DEPOSIT_WALLET_ADDRESS
};

const AppState = {
  account: "0x...",
  usdtBalance: "100",
  signer: {},
}

const ethers = {
  Contract: (address, abi, signer) => {
    // Mock implementation for demonstration purposes
    return {
      transfer: async (to, amount) => ({ hash: "0x..." }),
    }
  },
  utils: {
    parseUnits: (amount, decimals) => {
      // Mock implementation for demonstration purposes
      return amount * Math.pow(10, decimals)
    },
  },
}

class DepositManager {
  constructor() {
    this.isProcessing = false
    this.initializeEvents()
  }

  initializeEvents() {
    const form = document.getElementById("formDeposito")
    if (form) {
      form.addEventListener("submit", (e) => this.handleDeposit(e))
    }

    const input = document.getElementById("valorDeposito")
    if (input) {
      input.addEventListener("input", (e) => this.validateDepositAmount(e.target.value))
    }
  }

  validateDepositAmount(amount) {
    const numAmount = Number.parseFloat(amount)
    const input = document.getElementById("valorDeposito")
    const btn = document.getElementById("depositBtn")

    if (!amount || numAmount < WALLET_CONFIG.MIN_DEPOSIT_AMOUNT) {
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

  async handleDeposit(e) {
    e.preventDefault()

    if (this.isProcessing) return

    const amount = document.getElementById("valorDeposito").value

    if (!this.validateDepositAmount(amount)) {
      this.showDepositStatus("error", `Valor mínimo de depósito é ${WALLET_CONFIG.MIN_DEPOSIT_AMOUNT} USDT`)
      return
    }

    try {
      this.isProcessing = true
      this.setDepositLoading(true)
      this.showDepositStatus("processing", "Processando transação...")

      // Criar contrato USDT
      const contract = new ethers.Contract(WALLET_CONFIG.USDT_CONTRACT_ADDRESS, WALLET_CONFIG.USDT_ABI, AppState.signer)

      // Converter para unidades do contrato (USDT tem 6 decimais)
      const amountInWei = ethers.utils.parseUnits(amount, 6)

      // Executar transferência
      const tx = await contract.transfer(WALLET_CONFIG.DEPOSIT_WALLET_ADDRESS, amountInWei)

      this.showDepositStatus("processing", `Transação enviada. Hash: ${tx.hash}<br>Aguardando confirmação...`)

      // Aguardar confirmação
      const receipt = await tx.wait()

      if (receipt.status === 1) {
        // Enviar para backend
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
      console.error("Erro no depósito:", error)
      this.showDepositStatus("error", `Erro: ${error.message}`)
    } finally {
      this.isProcessing = false
      this.setDepositLoading(false)
    }
  }

  async sendDepositToBackend(payload) {
    try {
      const response = await fetch(WALLET_CONFIG.API_ENDPOINTS.DEPOSIT, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      })

      if (response.ok) {
        this.showDepositStatus("success", "Depósito realizado com sucesso! Seu saldo será atualizado em breve.")
        document.getElementById("valorDeposito").value = ""

        // Atualizar saldos
        if (window.walletManager) {
          await window.walletManager.updateBalances()
        }
      } else {
        const errorData = await response.json()
        throw new Error(errorData.error || "Erro ao processar no backend")
      }
    } catch (error) {
      throw new Error(`Erro no backend: ${error.message}`)
    }
  }

  showDepositStatus(type, message) {
    const container = document.getElementById("statusDeposito")
    if (!container) return

    const etherscanLink = message.includes("Hash:")
      ? message.replace(
          /Hash: (0x[a-fA-F0-9]{64})/,
          `Hash: <a href="https://etherscan.io/tx/$1" target="_blank" class="etherscan-link">$1</a>`,
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
    const container = document.getElementById("statusDeposito")
    if (container) {
      container.innerHTML = ""
    }
  }

  setDepositLoading(loading) {
    const btn = document.getElementById("depositBtn")
    if (btn) {
      btn.disabled = loading
      btn.innerHTML = loading ? '<span class="loading"></span>Processando...' : "Solicitar Depósito"
    }
  }
}

// Inicializar gerenciador de depósitos
let depositManager
document.addEventListener("DOMContentLoaded", () => {
  depositManager = new DepositManager()
})
