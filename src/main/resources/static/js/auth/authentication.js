const authCard = document.getElementById("auth-card");
const goToSignup = document.getElementById("go-to-signup");
const goToLogin = document.getElementById("go-to-login");

goToSignup.addEventListener("click", (e) => {
    e.preventDefault();
    authCard.classList.add("sign-up-mode");
});

goToLogin.addEventListener("click", (e) => {
    e.preventDefault();
    authCard.classList.remove("sign-up-mode");
});

const forgotPwdSpan = document.getElementById("forgot-password-button");
const resetPwdForm = document.getElementById("reset-password-form");
const cancelResetBtn = document.getElementById("cancel-reset-button");
const emailInputReset = document.getElementById("reset-password-email");

const resetSubmitBtn = resetPwdForm ? resetPwdForm.querySelector('button[type="submit"]') : null;

if (forgotPwdSpan && resetPwdForm && resetSubmitBtn) {
    const checkEmailEmpty = () => {
        if (emailInputReset.value.trim() === "") {
            resetSubmitBtn.disabled = true;
        } else {
            resetSubmitBtn.disabled = false;
        }
    };

    forgotPwdSpan.addEventListener("click", () => {
        resetPwdForm.classList.add("show");
        checkEmailEmpty();
        emailInputReset.focus();
    });

    // Lắng nghe thao tác gõ của người dùng
    emailInputReset.addEventListener("input", () => {
        checkEmailEmpty();
    });

    if (cancelResetBtn) {
        cancelResetBtn.addEventListener("click", () => {
            resetPwdForm.classList.remove("show");
            emailInputReset.value = "";
            checkEmailEmpty()
        });
    }
}
