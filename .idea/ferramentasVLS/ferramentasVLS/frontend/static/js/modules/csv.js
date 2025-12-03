class CsvModule {
    constructor() {
        this.fileInput = document.getElementById('fileInput');
        this.uploadArea = document.getElementById('uploadArea');
        this.form = document.getElementById('uploadForm');
        this.loading = document.getElementById('loading');
        this.result = document.getElementById('result');
        this.progress = document.getElementById('progress');

        this.init();
    }

    init() {
        this.uploadArea?.addEventListener('click', () => this.fileInput?.click());
        this.fileInput?.addEventListener('change', (e) => this.handleFile(e.target.files[0]));
        this.form?.addEventListener('submit', (e) => this.handleSubmit(e));

        this.uploadArea?.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.currentTarget.classList.add('dragover');
        });

        this.uploadArea?.addEventListener('dragleave', (e) => {
            e.currentTarget.classList.remove('dragover');
        });

        this.uploadArea?.addEventListener('drop', (e) => {
            e.preventDefault();
            e.currentTarget.classList.remove('dragover');
            this.handleFile(e.dataTransfer.files[0]);
        });
    }

    handleFile(file) {
        if (!file) return;

        if (!file.name.toLowerCase().endsWith('.csv')) {
            toast.error('Apenas arquivos CSV são permitidos');
            return;
        }

        this.selectedFile = file;
        this.showFileInfo(file);
    }

    showFileInfo(file) {
        const fileList = document.getElementById('fileList');
        if (fileList) {
            const size = (file.size / 1024).toFixed(2);
            fileList.innerHTML = `
                <div class="file-item">
                    <div class="file-info">
                        <span class="file-icon">📄</span>
                        <div>
                            <div class="file-name">${file.name}</div>
                            <div class="file-size">${size} KB</div>
                        </div>
                    </div>
                    <button type="button" class="file-remove" onclick="csvModule.clearFile()">×</button>
                </div>
            `;
            fileList.classList.remove('hidden');
        }
    }

    clearFile() {
        this.selectedFile = null;
        if (this.fileInput) this.fileInput.value = '';
        const fileList = document.getElementById('fileList');
        if (fileList) fileList.classList.add('hidden');
    }

    async handleSubmit(e) {
        e.preventDefault();

        if (!this.selectedFile) {
            toast.warning('Selecione um arquivo CSV');
            return;
        }

        loader.show(this.loading);
        this.result?.classList.add('hidden');

        const progressBar = new Progress(this.progress);
        progressBar.reset();

        try {
            const response = await api.csv.process(this.selectedFile, (percent) => {
                progressBar.set(percent);
            });

            progressBar.complete();
            this.showResult(response.data);
            toast.success('CSV processado com sucesso!');
        } catch (error) {
            toast.error('Erro ao processar CSV: ' + error.message);
        } finally {
            loader.hide(this.loading);
        }
    }

    showResult(data) {
        const resultDiv = this.result;
        if (resultDiv) {
            resultDiv.innerHTML = `
                <div class="alert alert-success">
                    <h3 style="font-size: 1.25rem; font-weight: bold; margin-bottom: 1rem;">
                        ✓ Arquivo processado com sucesso!
                    </h3>
                    <p><strong>Arquivo:</strong> ${data.fileName}</p>
                    <p><strong>Linhas processadas:</strong> ${data.linhasProcessadas}</p>
                    <button class="btn btn-primary" onclick="csvModule.download('${data.fileName}')">
                        ⬇️ Baixar CSV Formatado
                    </button>
                </div>
            `;
            resultDiv.classList.remove('hidden');
        }
    }

    async download(fileName) {
        try {
            await api.csv.download(fileName);
            toast.success('Download iniciado!');
        } catch (error) {
            toast.error('Erro no download: ' + error.message);
        }
    }
}