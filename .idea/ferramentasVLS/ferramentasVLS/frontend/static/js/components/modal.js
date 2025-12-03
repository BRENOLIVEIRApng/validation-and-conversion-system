class Modal {
    constructor() {
        this.overlay = null;
    }

    show(title, content, onConfirm = null, confirmText = 'Confirmar', cancelText = 'Cancelar') {
        this.overlay = this.createOverlay(title, content, onConfirm, confirmText, cancelText);
        document.body.appendChild(this.overlay);

        setTimeout(() => {
            this.overlay.classList.add('active');
        }, 10);

        this.overlay.addEventListener('click', (e) => {
            if (e.target === this.overlay) {
                this.hide();
            }
        });
    }

    createOverlay(title, content, onConfirm, confirmText, cancelText) {
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';

        const hasConfirm = typeof onConfirm === 'function';

        overlay.innerHTML = `
            <div class="modal">
                <div class="modal-header">
                    <h2 class="modal-title">${title}</h2>
                    <button class="modal-close" onclick="window.modal.hide()">×</button>
                </div>
                <div class="modal-body">
                    ${content}
                </div>
                ${hasConfirm ? `
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="window.modal.hide()">${cancelText}</button>
                        <button class="btn btn-primary modal-confirm">${confirmText}</button>
                    </div>
                ` : ''}
            </div>
        `;

        if (hasConfirm) {
            const confirmBtn = overlay.querySelector('.modal-confirm');
            confirmBtn.addEventListener('click', () => {
                onConfirm();
                this.hide();
            });
        }

        return overlay;
    }

    hide() {
        if (this.overlay) {
            this.overlay.classList.remove('active');
            setTimeout(() => {
                this.overlay.remove();
                this.overlay = null;
            }, 300);
        }
    }

    confirm(title, message, onConfirm) {
        this.show(title, `<p>${message}</p>`, onConfirm);
    }

    alert(title, message) {
        this.show(title, `<p>${message}</p>`);
    }
}

const modal = new Modal();
window.modal = modal;