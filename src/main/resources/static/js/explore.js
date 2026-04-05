const exploreBtnNav = document.getElementById("nav-explore-btn");
const exploreOverlay = document.getElementById("explore-overlay");
const exploreCloseBtn = document.getElementById("explore-close-btn");
const exploreForm = document.getElementById("explore-form");
const exploreSelect = document.getElementById("explore-select");
const exploreInput = document.querySelector(".explore-input");

if (exploreBtnNav && exploreOverlay) {
    // Mở Overlay
    exploreBtnNav.addEventListener("click", (e) => {
        e.preventDefault(); // Ngăn trình duyệt nhảy trang
        exploreOverlay.classList.add("show");

        // Đợi overlay mở xong (111ms)
        setTimeout(() => {
            exploreInput.focus();
        }, 111);
    });

    //Đóng Overlay
    exploreCloseBtn.addEventListener("click", () => {
        exploreOverlay.classList.remove("show");
    });

    // Đóng Overlay khi nhấn ESC
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && exploreOverlay.classList.contains("show")) {
            exploreOverlay.classList.remove("show");
        }
    });

    // Đổi endpoint API khi chọn Selection
    exploreSelect.addEventListener("change", (e) => {
        const selectedValue = e.target.value;
        if (selectedValue === "user") {
            exploreForm.action = "/search/user"; // Đường dẫn tìm kiếm user
        } else {
            exploreForm.action = "/search/topic"; // Đường dẫn tìm kiếm title
        }
    });
}
