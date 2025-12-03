// ===== MAIN.JS - Inicialização e Utilitários Globais =====

// Espera o DOM carregar
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Sistema de Ferramentas Úteis - Inicializado');

    // Inicializa componentes globais
    initGlobalComponents();

    // Carrega componentes HTML (header/footer) se necessário
    loadHTMLComponents();

    // Inicializa tooltips e outros elementos interativos
    initInteractiveElements();

    // Adiciona classes de animação aos elementos
    initAnimations();
});

// ===== Inicialização de Componentes Globais =====
function initGlobalComponents() {
    // Toast já foi inicializado em toast.js
    // Modal já foi inicializado em modal.js
    // Loader já foi inicializado em loader.js

    console.log('✅ Componentes globais carregados');
}

// ===== Carregamento de Componentes HTML =====
async function loadHTMLComponents() {
    // Carrega header se existir placeholder
    const headerPlaceholder = document.getElementById('header-placeholder');
    if (headerPlaceholder) {
        try {
            const response = await fetch('static/components/header.html');
            const html = await response.text();
            headerPlaceholder.innerHTML = html;
        } catch (error) {
            console.warn('Header component não encontrado');
        }
    }

    // Carrega footer se existir placeholder
    const footerPlaceholder = document.getElementById('footer-placeholder');
    if (footerPlaceholder) {
        try {
            const response = await fetch('static/components/footer.html');
            const html = await response.text();
            footerPlaceholder.innerHTML = html;
        } catch (error) {
            console.warn('Footer component não encontrado');
        }
    }
}

// ===== Inicialização de Elementos Interativos =====
function initInteractiveElements() {
    // Auto-focus no primeiro input de formulários
    const firstInput = document.querySelector('input[type="text"]:not([readonly]), textarea:not([readonly])');
    if (firstInput) {
        firstInput.focus();
    }

    // Adiciona animação de ripple em botões
    document.querySelectorAll('.btn').forEach(button => {
        button.addEventListener('click', createRipple);
    });

    // Previne submit duplo em formulários
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn && submitBtn.disabled) {
                e.preventDefault();
                return false;
            }
            if (submitBtn) {
                submitBtn.disabled = true;
                setTimeout(() => {
                    submitBtn.disabled = false;
                }, 2000);
            }
        });
    });
}

// ===== Efeito Ripple em Botões =====
function createRipple(event) {
    const button = event.currentTarget;
    const circle = document.createElement('span');
    const diameter = Math.max(button.clientWidth, button.clientHeight);
    const radius = diameter / 2;

    circle.style.width = circle.style.height = `${diameter}px`;
    circle.style.left = `${event.clientX - button.offsetLeft - radius}px`;
    circle.style.top = `${event.clientY - button.offsetTop - radius}px`;
    circle.classList.add('ripple');

    const ripple = button.getElementsByClassName('ripple')[0];
    if (ripple) {
        ripple.remove();
    }

    button.appendChild(circle);
}

// ===== Inicialização de Animações =====
function initAnimations() {
    // Adiciona animações de entrada aos cards
    const cards = document.querySelectorAll('.card, .feature-card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';

        setTimeout(() => {
            card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
}

// ===== Utilitários Globais =====

// Formatar CPF
window.formatCPF = function(value) {
    value = value.replace(/\D/g, '');
    if (value.length <= 11) {
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }
    return value;
};

// Formatar data para DD/MM/YYYY
window.formatDate = function(date) {
    if (date instanceof Date) {
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    }
    return date;
};

// Formatar bytes para tamanho legível
window.formatFileSize = function(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
};

// Debounce function
window.debounce = function(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
};

// Throttle function
window.throttle = function(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
};

// Copiar texto para clipboard
window.copyToClipboard = async function(text) {
    try {
        await navigator.clipboard.writeText(text);
        toast.success('Copiado para a área de transferência!');
        return true;
    } catch (err) {
        // Fallback para navegadores antigos
        const textarea = document.createElement('textarea');
        textarea.value = text;
        textarea.style.position = 'fixed';
        textarea.style.opacity = '0';
        document.body.appendChild(textarea);
        textarea.select();

        try {
            document.execCommand('copy');
            toast.success('Copiado para a área de transferência!');
            return true;
        } catch (err) {
            toast.error('Erro ao copiar texto');
            return false;
        } finally {
            document.body.removeChild(textarea);
        }
    }
};

// Validar email
window.isValidEmail = function(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
};

// Sanitizar string para uso seguro
window.sanitizeString = function(str) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#x27;',
        "/": '&#x2F;',
    };
    const reg = /[&<>"'/]/ig;
    return str.replace(reg, (match) => map[match]);
};

// Scroll suave para elemento
window.scrollToElement = function(element, offset = 0) {
    if (typeof element === 'string') {
        element = document.querySelector(element);
    }
    if (element) {
        const top = element.getBoundingClientRect().top + window.pageYOffset - offset;
        window.scrollTo({
            top: top,
            behavior: 'smooth'
        });
    }
};

// Verificar se elemento está visível no viewport
window.isInViewport = function(element) {
    if (typeof element === 'string') {
        element = document.querySelector(element);
    }
    if (!element) return false;

    const rect = element.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
};

// Adicionar classe quando elemento entra no viewport
window.observeElement = function(element, className = 'in-view') {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add(className);
            }
        });
    });

    if (typeof element === 'string') {
        document.querySelectorAll(element).forEach(el => observer.observe(el));
    } else {
        observer.observe(element);
    }
};

// Gerar ID único
window.generateId = function() {
    return 'id_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
};

// Delay promise
window.sleep = function(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
};

// ===== Tratamento de Erros Global =====
window.addEventListener('error', function(event) {
    console.error('Erro global capturado:', event.error);
});

window.addEventListener('unhandledrejection', function(event) {
    console.error('Promise rejeitada não tratada:', event.reason);
    toast.error('Ocorreu um erro inesperado. Tente novamente.');
});

// ===== Mensagem de Console para Desenvolvedores =====
console.log('%c🛠️ Sistema de Ferramentas Úteis', 'color: #10b981; font-size: 20px; font-weight: bold;');
console.log('%cVersão: 2.0.0', 'color: #6b7280; font-size: 12px;');
console.log('%cDesenvolvido com ❤️', 'color: #ef4444; font-size: 12px;');

// Exportar funções para uso global
window.app = {
    formatCPF: window.formatCPF,
    formatDate: window.formatDate,
    formatFileSize: window.formatFileSize,
    copyToClipboard: window.copyToClipboard,
    debounce: window.debounce,
    throttle: window.throttle,
    isValidEmail: window.isValidEmail,
    sanitizeString: window.sanitizeString,
    scrollToElement: window.scrollToElement,
    isInViewport: window.isInViewport,
    observeElement: window.observeElement,
    generateId: window.generateId,
    sleep: window.sleep
};