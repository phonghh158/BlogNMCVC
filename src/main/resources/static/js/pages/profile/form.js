const avatarBox = document.getElementById("avatar-box");
const avatarInput = document.getElementById("avatar-input");
const avatarPreview = document.getElementById("avatar-preview");
const avatarActions = document.getElementById("avatar-actions");
const btnCancelAvatar = document.getElementById("btn-cancel-avatar");

let originalAvatarSrc = avatarPreview.src;

avatarBox.addEventListener("click", () => {
    avatarInput.click();
});

avatarInput.addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (event) => {
            avatarPreview.src = event.target.result;
            avatarActions.classList.add("show");
        };
        reader.readAsDataURL(file);
    }
});

// Bấm nút Hủy Avatar
btnCancelAvatar.addEventListener("click", () => {
    avatarInput.value = "";
    avatarPreview.src = originalAvatarSrc;
    avatarActions.classList.remove("show");
});


const profileContainer = document.getElementById("profile-container");
const editableFields = document.querySelectorAll(".editable-field");

let isEditingAny = false; // Cờ theo dõi xem có trường nào đang được sửa không

editableFields.forEach((field) => {
    const btnEdit = field.querySelector(".btn-trigger-edit");
    const btnCancel = field.querySelector(".btn-cancel-edit");
    const form = field.querySelector("form");

    btnEdit.addEventListener("click", () => {
        if (isEditingAny) return;

        isEditingAny = true;
        profileContainer.classList.add("is-editing");
        field.classList.add("editing");

        const firstInput = form.querySelector("input, textarea");
        if (firstInput) firstInput.focus();
    });

    btnCancel.addEventListener("click", () => {
        isEditingAny = false;
        profileContainer.classList.remove("is-editing");
        field.classList.remove("editing");

        form.reset();
    });
});


const fieldUsername = document.getElementById("field-username");

if (fieldUsername) {
    const usernameInput = fieldUsername.querySelector('input[name="username"]');
    const btnSubmitUsername = fieldUsername.querySelector('button[type="submit"]');
    const btnEditUsername = fieldUsername.querySelector(".btn-trigger-edit");

    const originalUsername = usernameInput.value.trim();

    const checkUsernameValidity = () => {
        const currentValue = usernameInput.value.trim();

        // Điều kiện mở khóa nút:
        // 1. Phải có từ 3 ký tự trở lên
        // 2. Phải khác với username cũ
        if (currentValue.length >= 3 && currentValue !== originalUsername) {
            btnSubmitUsername.disabled = false;
        } else {
            btnSubmitUsername.disabled = true;
        }
    };

    btnEditUsername.addEventListener("click", () => {
        checkUsernameValidity();
    });

    usernameInput.addEventListener("input", () => {
        checkUsernameValidity();
    });
}

const fieldName = document.getElementById("field-name");

if (fieldName) {
    const nameInput = fieldName.querySelector('input[name="name"]');
    const btnSubmitName = fieldName.querySelector('button[type="submit"]');
    const btnEditName = fieldName.querySelector(".btn-trigger-edit");

    const originalName = nameInput.value.trim();

    const checkNameValidity = () => {
        const currentValue = nameInput.value.trim();
        if (currentValue.length >= 1 && currentValue !== originalName) {
            btnSubmitName.disabled = false;
        } else {
            btnSubmitName.disabled = true;
        }
    };

    btnEditName.addEventListener("click", () => {
        checkNameValidity();
    });

    nameInput.addEventListener("input", () => {
        checkNameValidity();
    });
}

// Thông báo advanced setting
