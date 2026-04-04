// js/change-password.js

const changePwdForm = document.getElementById("change-pwd-form");
const newPasswordInput = document.getElementById("new-password");
const confirmPasswordInput = document.getElementById("confirm-password");
const validationMsg = document.getElementById("validation-msg");

changePwdForm.addEventListener("submit", (e) => {
    const newPassword = newPasswordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    if (newPassword !== confirmPassword) {
        e.preventDefault();
        validationMsg.classList.add("show");
        confirmPasswordInput.focus();
    } else {
        validationMsg.classList.remove("show");
    }
});

confirmPasswordInput.addEventListener("input", () => {
    validationMsg.classList.remove("show");
});

newPasswordInput.addEventListener("input", () => {
    validationMsg.classList.remove("show");
});

