const collectionFilterBox = document.getElementById("filter-collection");
const statusFilterBox = document.getElementById("filter-status");

function updateFilterState(filterBox) {
    if (!filterBox) {
        return;
    }

    const filterOptions = filterBox.querySelectorAll(".filter-option");
    const filterClear = filterBox.querySelector(".filter-clear");

    let hasActive = false;

    filterOptions.forEach((option) => {
        const input = option.querySelector("input");

        if (input && input.checked) {
            option.classList.add("active");
            hasActive = true;
        } else {
            option.classList.remove("active");
        }
    });

    if (filterClear) {
        filterClear.classList.toggle("show", hasActive);
    }
}

function bindFilterEvents(filterBox) {
    if (!filterBox) {
        return;
    }

    const inputs = filterBox.querySelectorAll(".filter-option input");

    inputs.forEach((input) => {
        input.addEventListener("change", () => {
            updateFilterState(filterBox);
            input.form.submit();
        });
    });

    updateFilterState(filterBox);
}

bindFilterEvents(collectionFilterBox);
bindFilterEvents(statusFilterBox);

const pageInput = document.getElementById("current-page-input");

if (pageInput) {
    pageInput.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();

            let page = parseInt(this.value);

            if (isNaN(page)) return;

            const maxPage = parseInt(this.max);

            // clamp value
            if (page < 1) page = 1;
            if (page > maxPage) page = maxPage;

            const params = new URLSearchParams(window.location.search);

            // Spring dùng 0-based
            params.set("page", page - 1);

            window.location.href = "/topics?" + params.toString();
        }
    });
}