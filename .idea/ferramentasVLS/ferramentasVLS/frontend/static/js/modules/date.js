class DateModule {
    constructor() {
        this.input = document.getElementById('dateInput');
        this.output = document.getElementById('dateOutput');
        this.btn = document.getElementById('convertBtn');
        this.clearBtn = document.getElementById('clearBtn');
        this.copyBtn = document.getElementById('copyBtn');
        this.loading = document.getElementById('loading');
        this.result = document.getElementById('result');
        this.counter = document.getElementById('lineCounter');

        this.init();
    }

    init() {
        this.input?.addEventListener('input', () => this.updateCounter());
        this.btn?.addEventListener('click', () => this.convert());
        this.clearBtn?.addEventListener('click', () => this.clear());
        this.copyBtn?.addEventListener('click', () => this.copy());
    }

    updateCounter() {
        const lines = this.input.value.trim().split('\n').filter(l => l.trim()).length;
        if (this.counter) {
            this.counter.textContent = lines;
        }
    }

    async convert() {
        const datas = this.input?.value;

        if (!datas?.trim()) {
            toast.warning('Por favor, insira pelo menos uma data');
            return;
        }

        loader.show(this.loading);
        this.result?.classList.add('hidden');

        try {
            const response = await api.dates.convert(datas);
            const data = response.data;

            this.showResult(data);
            toast.success(`${data.totalConvertidas} datas convertidas!`);
        } catch (error) {
            toast.error('Erro ao converter datas: ' + error.message);
        } finally {
            loader.hide(this.loading);
        }
    }

    showResult(data) {
        if (this.output) {
            this.output.value = data.datasConvertidas.join('\n');
        }

        const validEl = document.getElementById('totalConvertidas');
        const errorEl = document.getElementById('totalErros');
        const errorList = document.getElementById('errorList');
        const errorArea = document.getElementById('errorArea');

        if (validEl) validEl.textContent = data.totalConvertidas;
        if (errorEl) errorEl.textContent = data.totalErros;

        if (data.erros.length > 0 && errorList) {
            errorList.innerHTML = data.erros.map(e => `<li>${e}</li>`).join('');
            errorArea?.classList.remove('hidden');
        } else {
            errorArea?.classList.add('hidden');
        }

        this.result?.classList.remove('hidden');
    }

    copy() {
        if (this.output) {
            this.output.select();
            document.execCommand('copy');
            toast.success('Datas copiadas!');
        }
    }

    clear() {
        if (this.input) this.input.value = '';
        if (this.output) this.output.value = '';
        this.result?.classList.add('hidden');
        this.updateCounter();
    }
}
