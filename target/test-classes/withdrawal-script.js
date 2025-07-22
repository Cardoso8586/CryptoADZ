// SCRIPT DE SAQUE
const WALLET_CONFIG = {
  MIN_WITHDRAWAL_AMOUNT: 10,
  WITHDRAWAL_FEE: 2,
  API_ENDPOINTS: {
    WITHDRAWAL: "/api/withdrawal",
  },
}

const AppState = {
  account: "0x1234567890abcdef",
  userBalance: 100,
}

class WithdrawalManager {
  constructor() {
    this.isProcessing = false
    this.initializeEvents()
  }

  initializeEvents() {
    const form = document.getElementById("formRetirada")
    if (form) {
      form.addEventListener("submit", (e) => this.handleWithdrawal(e))
    }

    const amountInput = document.getElementById("valorRetirada")
    if (amountInput) {
      amountInput.addEventListener("input", (e) => this.updateFeePreview(e.target.value))
    }

    const addressInput = document.getElementById("enderecoDestino")
    if (addressInput) {
      addressInput.addEventListener("input", (e) => this.validateAddress(e.target.value))
    }
  }

  updateFeePreview(amount) {
    const numAmount = Number.parseFloat(amount)
    const feeWarning = document.getElementById("avisoTaxa")
    const netAmountSpan = document.getElementById("valorLiquidoPreview")

    if (numAmount >= WALLET_CONFIG.MIN_WITHDRAWAL_AMOUNT) {
      const netAmount = (numAmount - WALLET_CONFIG.WITHDRAWAL_FEE).toFixed(2)
      netAmountSpan.textContent = netAmount
      feeWarning.style.display = "block"
    } else {
      feeWarning.style.display = "none"
    }

    this.validateWithdrawalAmount(amount)
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

  validateWithdrawalAmount(amount) {
    const numAmount = Number.parseFloat(amount)
    const input = document.getElementById("valorRetirada")
    const btn = document.getElementById("withdrawBtn")

    if (!amount || numAmount < WALLET_CONFIG.MIN_WITHDRAWAL_AMOUNT) {
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

  async handleWithdrawal(e) {
    e.preventDefault()

    if (this.isProcessing) return

    const amount = document.getElementById("valorRetirada").value
    const address = document.getElementById("enderecoDestino").value

    // Validações
    if (!this.validateWithdrawalAmount(amount)) {
      this.showWithdrawalStatus("error", `Valor mínimo de saque é ${WALLET_CONFIG.MIN_WITHDRAWAL_AMOUNT} USDT`)
      return
    }

    if (!this.validateAddress(address)) {
      this.showWithdrawalStatus("error", "Endereço de carteira inválido")
      return
    }

    try {
      this.isProcessing = true
      this.setWithdrawalLoading(true)
      this.showWithdrawalStatus("processing", "Processando solicitação de saque...")

      const numAmount = Number.parseFloat(amount)
      const netAmount = numAmount - WALLET_CONFIG.WITHDRAWAL_FEE

      // Enviar solicitação para backend
      const response = await fetch(WALLET_CONFIG.API_ENDPOINTS.WITHDRAWAL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userAddress: AppState.account,
          destinationAddress: address,
          amount: numAmount,
          netAmount: netAmount,
          fee: WALLET_CONFIG.WITHDRAWAL_FEE,
        }),
      })

      if (response.ok) {
        const data = await response.json()
        this.showWithdrawalStatus(
          "success",
          `Saque solicitado com sucesso!<br>
                     Você receberá: ${netAmount.toFixed(2)} USDT<br>
                     ID: ${data.withdrawalId}<br>
                     Tempo estimado: ${data.estimatedTime || "24-48 horas"}`,
        )

        // Limpar formulário
        document.getElementById("valorRetirada").value = ""
        document.getElementById("enderecoDestino").value = ""
        document.getElementById("avisoTaxa").style.display = "none"

        // Atualizar saldos
        if (window.walletManager) {
          await window.walletManager.updateBalances()
        }
      } else {
        const errorData = await response.json()
        throw new Error(errorData.error || "Erro ao processar saque")
      }
    } catch (error) {
      console.error("Erro no saque:", error)
      this.showWithdrawalStatus("error", `Erro: ${error.message}`)
    } finally {
      this.isProcessing = false
      this.setWithdrawalLoading(false)
    }
  }

  showWithdrawalStatus(type, message) {
    const container = document.getElementById("statusRetirada")
    if (!container) return

    container.innerHTML = `
            <div class="status-message status-${type}">
                ${type === "processing" ? '<span class="loading"></span>' : ""}
                ${message}
            </div>
        `
  }

  clearWithdrawalStatus() {
    const container = document.getElementById("statusRetirada")
    if (container) {
      container.innerHTML = ""
    }
  }

  setWithdrawalLoading(loading) {
    const btn = document.getElementById("withdrawBtn")
    if (btn) {
      btn.disabled = loading
      btn.innerHTML = loading ? '<span class="loading"></span>Processando...' : "Retirar"
    }
  }
}

// Inicializar gerenciador de saques
let withdrawalManager
document.addEventListener("DOMContentLoaded", () => {
  withdrawalManager = new WithdrawalManager()
})
