class CpfModule {
    constructor() {
        this.input = document.getElementById('cpfInput');
        this.btn = document.getElementById('validateBtn');
        this.clearBtn = document.getElementById('clearBtn');
        this.result = document.getElementById('result');
        this.loading = document.getElementById('loading');
        this.totalValidos = 0;
        this.totalInvalidos = 0;

        this.init();
    }

    init() {
        this.input?.addEventListener('input', (e) => this.formatCpf(e));
        this.input?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.validate();
        });
        this.btn?.addEventListener('click', () => this.validate());
        this.clearBtn?.addEventListener('click', () => this.clear());
    }

    formatCpf(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length <= 11) {
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
            e.target.value = value;
        }
    }

    async validate() {
        const cpf = this.input?.value;

        if (!cpf?.trim()) {
            toast.warning('Por favor, insira um CPF');
            return;
        }

        loader.show(this.loading);
        this.result.classList.add('hidden');

        try {
            const response = await api.cpf.validate(cpf);
            const data = response.data;

            this.showResult(data);

            if (data.valido) {
                this.totalValidos++;
                toast.success('CPF válido!');
            } else {
                this.totalInvalidos++;
                toast.error('CPF inválido!');
            }

            this.updateStats();
        } catch (error) {
            toast.error('Erro ao validar CPF: ' + error.message);
        } finally {
            loader.hide(this.loading);
        }
    }

    showResult(data) {
        const validClass = data.valido ? 'alert-success' : 'alert-error';
        const icon = data.valido ? '✓' : '✗';

        this.result.className = `alert ${validClass}`;
        this.result.innerHTML = `
            <div style="display: flex; align-items: center; gap: 1rem;">
                <div style="font-size: 2rem;">${icon}</div>
                <div style="flex: 1;">
                    <h3 style="font-size: 1.25rem; font-weight: bold; margin-bottom: 0.5rem;">
                        ${data.mensagem}
                    </h3>
                    <p style="font-family: monospace; font-size: 1.125rem;">
                        ${data.cpfFormatado}
                    </p>
                </div>
            </div>
        `;
        this.result.classList.remove('hidden');
    }

    updateStats() {
        const validEl = document.getElementById('totalValidos');
        const invalidEl = document.getElementById('totalInvalidos');
        const statsArea = document.getElementById('statsArea');

        if (validEl) validEl.textContent = this.totalValidos;
        if (invalidEl) invalidEl.textContent = this.totalInvalidos;
        if (statsArea) statsArea.classList.remove('hidden');
    }

    clear() {
        if (this.input) this.input.value = '';
        this.result?.classList.add('hidden');
        this.input?.focus();
    }
}