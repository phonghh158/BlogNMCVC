const pageInput = document.getElementById("current-page-input");

if (pageInput) {
    pageInput.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();

            let page = parseInt(this.value);

            if (isNaN(page)) return;

            const maxPage = parseInt(this.max);

            if (page < 1) page = 1;
            if (page > maxPage) page = maxPage;

            const params = new URLSearchParams(window.location.search);
            params.set("page", page - 1);

            if (!params.has("size")) {
                params.set("size", "6");
            }

            window.location.href = window.location.pathname + "?" + params.toString();
        }
    });
}