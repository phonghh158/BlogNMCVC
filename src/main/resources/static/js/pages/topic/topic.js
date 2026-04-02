console.log("topic js loaded");

async function toggleAction({ element, url, isActive, onSuccess }) {
    try {
        const response = await fetch(url, {
            method: isActive ? "DELETE" : "POST"
        });

        if (!response.ok) {
            throw new Error(`Request failed: ${response.status}`);
        }

        const data = await response.json();

        if (data.success) {
            element.classList.toggle("active");

            if (onSuccess) {
                onSuccess(data);
            }
        }
    } catch (error) {
        console.error("Error:", error);
    }
}

const reactionBtn = document.getElementById("reaction-button");
const bookmarkBtn = document.getElementById("bookmark-button");

if (reactionBtn) {
    reactionBtn.addEventListener("click", () => {
        const topicId = reactionBtn.dataset.topicId;
        const isActive = reactionBtn.classList.contains("active");

        toggleAction({
            element: reactionBtn,
            url: `/reactions/topics/${topicId}`,
            isActive,
            onSuccess: (data) => {
                console.log("count:", data.count);
            }
        });
    });
}

if (bookmarkBtn) {
    bookmarkBtn.addEventListener("click", () => {
        const topicId = bookmarkBtn.dataset.topicId;
        const isActive = bookmarkBtn.classList.contains("active");

        toggleAction({
            element: bookmarkBtn,
            url: `/bookmarks/topics/${topicId}`,
            isActive
        });
    });
}