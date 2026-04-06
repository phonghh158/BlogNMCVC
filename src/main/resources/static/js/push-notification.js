// js/push-notification.js
function showPushNotification(type = "info", title = "", message = "", duration = 3333) {
    const container = document.getElementById("push-notification-container");
    if (!container) return;

    const icons = {
        success: "check_circle",
        error: "error",
        warning: "warning",
        info: "info",
    };
    const iconName = icons[type] || icons["info"];

    const toast = document.createElement("div");
    toast.className = `push-notification-toast ${type}`;

    toast.innerHTML = `
        <span class="material-symbols-rounded push-notification-icon">${iconName}</span>
        <div class="push-notification-content">
            <span class="push-notification-title">${title}</span>
            <span class="push-notification-message">${message}</span>
        </div>
        <button class="push-notification-close-btn" title="Đóng">
            <span class="material-symbols-rounded">close</span>
        </button>
        <div class="push-notification-progress">
            <div class="push-notification-progress-bar"></div>
        </div>
    `;

    container.appendChild(toast);

    const progressBar = toast.querySelector(".push-notification-progress-bar");

    progressBar.style.transition = `transform ${duration}ms linear`;

    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            progressBar.style.transform = "scaleX(0)";
        });
    });

    const closeToast = () => {
        toast.classList.add("hide");
        toast.addEventListener("animationend", () => {
            toast.remove();
        });
    };

    const timer = setTimeout(closeToast, duration);

    toast.querySelector(".push-notification-close-btn").addEventListener("click", () => {
        clearTimeout(timer);
        closeToast();
    });
}


const serverPushNotification = document.getElementById("server-push-notification");

const type = serverPushNotification.dataset.type || "info";
const message = serverPushNotification.dataset.message || "";

// Load thông báo khi có message
document.addEventListener("DOMContentLoaded", () => {
    const serverPushNotification = document.getElementById("server-push-notification");

    if (!serverPushNotification) {
        return;
    }

    const type = serverPushNotification.dataset.type || "info";
    const message = serverPushNotification.dataset.message || "";

    if (!message.trim()) {
        return;
    }

    let title = "Thông báo";
    let duration = 3333; //ms
    if (type === "success") {
        title = "Thành công";
        duration = 5555;
    } else if (type === "error") {
        title = "Có lỗi nè";
        duration = 4444;
    }

    setTimeout(() => {
        showPushNotification(type, title, message, duration);
    }, 333);
});
