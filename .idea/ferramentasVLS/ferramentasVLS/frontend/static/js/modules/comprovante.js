class ComprovanteModule {
    constructor() {
        this.input = document.getElementById('jsonInput');
        this.btn = document.getElementById('generateBtn');
        this.clearBtn = document.getElementById('clearBtn');
        this.loading = document.getElementById('loading');
        this.result = document.getElementById('result');
        this.counter = document.getElementById('charCounter');

        this.init();
    }

    init() {
        this.input?.addEventListener('input', () => this.updateCounter());
        this.btn?.addEventListener('click', () => this.generate());
        this.clearBtn?.addEventListener('click', () => this.clear());
    }

    updateCounter() {
        if (this.counter) {
            this.counter.textContent = this.input.value.length;
        }
    }

    async generate() {
        const jsonRetorno = this.input?.value;

        if (!jsonRetorno?.trim()) {
            toast.warning('Por favor, insira o JSON retornado pela API');
            return;
        }

        loader.show(this.loading);
        this.result?.classList.add('hidden');

        try {
            await api.comprovante.generate(jsonRetorno);
            this.showSuccess();
            toast.success('PDF gerado com sucesso!');
        } catch (error) {
            toast.error('Erro ao gerar PDF: ' + error.message);
        } finally {
            loader.hide(this.loading);
        }
    }

    showSuccess() {
        if (this.result) {
            this.result.className = 'alert alert-success';
            this.result.innerHTML = `
                <h3 style="font-size: 1.25rem; font-weight: bold; margin-bottom: 0.5rem;">
                    ✓ PDF gerado com sucesso!
                </h3>
                <p>O download deve iniciar automaticamente.</p>
                <p style="font-size: 0.875rem; color: var(--text-secondary); margin-top: 0.5rem;">
                    Se o download não iniciar, verifique as configurações do seu navegador.
                </p>
            `;
            this.result.classList.remove('hidden');
        }
    }

    clear() {
        if (this.input) this.input.value = '';
        this.result?.classList.add('hidden');
        this.updateCounter();
    }
}

window.CpfModule = CpfModule;
window.DateModule = DateModule;
window.CsvModule = CsvModule;
window.ComprovanteModule = ComprovanteModule;