const thumbnailInput = document.getElementById("topic-thumbnail");
const previewImage = document.getElementById("preview-image");
const uploadBox = document.getElementById("thumbnail-upload-box");
const thumbnailError = document.getElementById("thumbnail-error");

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

thumbnailInput.addEventListener("change", function () {
    handleFile(this.files[0]);
});

uploadBox.addEventListener("dragover", (e) => {
    e.preventDefault();
    uploadBox.classList.add("dragover");
});

uploadBox.addEventListener("dragleave", (e) => {
    e.preventDefault();
    uploadBox.classList.remove("dragover");
});

uploadBox.addEventListener("drop", (e) => {
    e.preventDefault();
    uploadBox.classList.remove("dragover");

    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
        const file = e.dataTransfer.files[0];

        handleFile(file);

        if (!thumbnailInput.disabled) {
            thumbnailInput.files = e.dataTransfer.files;
        }
    }
});

function showThumbnailError(message) {
    thumbnailError.textContent = message;
    thumbnailError.style.display = "block";
}

function clearThumbnailError() {
    thumbnailError.textContent = "";
    thumbnailError.style.display = "none";
}

function resetThumbnailPreview() {
    thumbnailInput.value = "";
    previewImage.src = "";
    uploadBox.classList.remove("has-image");
}

function handleFile(file) {
    if (!file) {
        resetThumbnailPreview();
        return;
    }

    if (!file.type.startsWith("image/")) {
        alert("Vui lòng chọn đúng file ảnh.");
        resetThumbnailPreview();
        return;
    }

    if (file.size > MAX_FILE_SIZE) {
        alert("Ảnh phải nhỏ hơn hoặc bằng 10MB.");
        resetThumbnailPreview();
        return;
    }

    const reader = new FileReader();

    reader.onload = function (e) {
        previewImage.src = e.target.result;
        uploadBox.classList.add("has-image");
    };

    reader.readAsDataURL(file);
}

const contentInput = document.getElementById("content");

contentInput.addEventListener("input", function () {
    this.style.height = "auto"; // reset
    this.style.height = this.scrollHeight + "px"; // set theo nội dung
});

// Set status
function setStatus(value) {
    const hidden = document.getElementById("status-hidden");
    if (hidden) {
        hidden.setAttribute("value", value);
    }
}
