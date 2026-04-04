const rainbowColor = [
    "#ffb3b3",
    "#ffd1a9",
    "#fff2b3",
    "#c8f2c2",
    "#bbdcff",
    "#cfc8ff",
    "#e6c6ff",
];

const collectionList = document.querySelector(".collection-list");
const collectionItems = collectionList.querySelectorAll(".collection-item");

let arrow = document.getElementById("arrow");
let position = 0;

for (let i = 0; i < collectionItems.length; i++) {
    collectionItems[i].style.backgroundColor = rainbowColor[i % rainbowColor.length];
    position += Math.floor(collectionItems[i].clientHeight / 2);
    const arrowPosition = position;
    collectionItems[i].addEventListener("click", () => {
        let currentActiveCollection = document.querySelector(
            ".collection-list .collection-item.active",
        );
        currentActiveCollection.classList.remove("active");

        collectionItems[i].classList.add("active");

        arrow.style.top = arrowPosition + "px";
    });

    position += Math.ceil(collectionItems[i].clientHeight / 2) + 24;
}

arrow.style.top = Math.floor(collectionItems[0].clientHeight / 2) + "px";

// Add Content
const describeContainer = document.getElementById("collection-describe-text");
const topicsContainer = document.querySelector(".collection-topics");

function escapeHtml(text) {
    if (!text) {
        return "";
    }

    return text
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function stripHtml(html) {
    if (!html) {
        return "";
    }

    const temp = document.createElement("div");
    temp.innerHTML = html;
    return temp.textContent || temp.innerText || "";
}

function truncateText(text, maxLength) {
    if (!text) {
        return "";
    }

    if (text.length <= maxLength) {
        return text;
    }

    return text.substring(0, maxLength).trim() + "...";
}

function formatDate(dateString) {
    if (!dateString) {
        return "";
    }

    const date = new Date(dateString);

    if (Number.isNaN(date.getTime())) {
        return "";
    }

    return new Intl.DateTimeFormat("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    }).format(date);
}

function generateTopicCards(topics) {
    if (!topics || topics.length === 0) {
        return `
            <div class="topic-empty">
                Chưa có bài viết nổi bật nào.
            </div>
        `;
    }

    let htmlContent = "";

    for (let i = 0; i < topics.length; i++) {
        const topic = topics[i];
        const summary = truncateText(stripHtml(topic.content), 180);
        const topicDate = formatDate(topic.publishedAt || topic.createdAt);

        htmlContent += `
            <a class="topic-card" href="/topics/${topic.slug}">
                <div class="topic-image">
                    <img src="${topic.thumbnail || "/images/pages/homepage/topics/topic-1.png"}" alt="${escapeHtml(topic.title)}" />
                </div>
                <div class="topic-meta">
                    <span class="topic-collection">${escapeHtml(topic.collectionName || "")}</span>
                    <span class="topic-date">${escapeHtml(topicDate)}</span>
                </div>
                <span class="topic-title">${escapeHtml(topic.title || "")}</span>
                <span class="topic-summary">${escapeHtml(summary)}</span>
            </a>
        `;
    }

    return htmlContent;
}

function renderCollectionContent(data) {
    if (!data || !data.collection) {
        describeContainer.innerText = "Không thể tải nội dung collection.";
        topicsContainer.innerHTML = "";
        return;
    }

    const collection = data.collection;
    const topics = data.topics || [];

    describeContainer.innerText = collection.description || "";
    topicsContainer.innerHTML = generateTopicCards(topics);
}

async function fetchCollectionContent(collectionId) {
    try {
        const response = await fetch(`/collections/${collectionId}/detail`);

        if (!response.ok) {
            throw new Error("Failed to fetch collection detail");
        }

        const data = await response.json();
        renderCollectionContent(data);
    } catch (error) {
        describeContainer.innerText = "Không thể tải nội dung collection.";
        topicsContainer.innerHTML = `
            <div class="topic-empty">
                Đã có lỗi xảy ra khi tải bài viết nổi bật.
            </div>
        `;
        console.error(error);
    }
}

for (let i = 0; i < collectionItems.length; i++) {
    collectionItems[i].addEventListener("click", () => {
        const collectionId = collectionItems[i].dataset.id;

        const editCollection = document.getElementById("edit-collection");
        const deleteCollection = document.getElementById("delete-collection");

        if (editCollection) {
            editCollection.setAttribute("href", `/admin/collections/${collectionId}/edit`);
        }

        if (deleteCollection) {
            deleteCollection.setAttribute("href", `/admin/collections/${collectionId}/delete`);
        }

        fetchCollectionContent(collectionId);
    });
}

if (collectionItems.length > 0) {
    const firstCollectionId = collectionItems[0].dataset.id;
    fetchCollectionContent(firstCollectionId);
}