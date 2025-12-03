class Progress {
    constructor(element) {
        this.element = typeof element === 'string' ? document.querySelector(element) : element;
        this.bar = this.element?.querySelector('.progress-bar');
    }

    set(percent) {
        if (this.bar) {
            this.bar.style.width = `${Math.min(100, Math.max(0, percent))}%`;
        }
    }

    reset() {
        this.set(0);
    }

    complete() {
        this.set(100);
    }
}

window.Progress = Progress;