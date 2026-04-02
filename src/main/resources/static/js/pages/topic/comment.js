const COMMENT_PAGE_SIZE = 5;

let currentPage = 0;
let totalPages = 1;
let editingCommentId = null;

function getContainer() {
    return document.getElementById("comment-container");
}

function getList() {
    return document.getElementById("comment-list");
}

function getTopicId() {
    return getContainer().dataset.topicId;
}

function getCurrentUserId() {
    return getContainer().dataset.currentUserId;
}

function isAdmin() {
    return getContainer().dataset.isAdmin === "true";
}

function getAuthorReactions() {
    const value = getContainer().dataset.authorReactions;
    if (!value) {
        return [];
    }

    return value
        .replace(/^\[/, "")
        .replace(/\]$/, "")
        .split(",")
        .map(item => item.trim())
        .filter(Boolean);
}

function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text ?? "";
    return div.innerHTML;
}

function formatContent(content) {
    return escapeHtml(content).replace(/\n/g, "<br>");
}

function getReactionImage(type) {
    if (!type) {
        return "";
    }

    return `/images/author-reaction/${type.toLowerCase()}.png`;
}

function getReactionLabel(type) {
    const map = {
        HUG: "Hug",
        LOVE: "Love",
        CHILL: "Chill",
        EHEHE: "Ê hê hê",
        SHOCK: "Shock",
        UNCOMFORTABLE: "Uncomfortable",
        ANGRY: "Angry",
        FUFU: "fufu"
    };

    return map[type] || type;
}

function buildComment(comment) {
    const userId = getCurrentUserId();
    const admin = isAdmin();
    const isOwner = userId && String(comment.commenterId) === String(userId);

    return `
        <div class="comment" id="comment-${comment.id}">
            <div class="comment-header">
                <div class="commenter-info">
                    <div class="commenter-avatar">
                        <img src="${comment.commenterAvatar || "/images/default-avatar.jpg"}" />
                    </div>
                    <a class="commenter-name">${escapeHtml(comment.commenterName || comment.commenterUsername)}</a>
                </div>

                <span class="comment-date">${formatCommentDate(comment.createdAt)}</span>

                ${admin ? `
                    <div class="admin-react">
                        <span class="material-symbols-rounded admin-react-button">add_reaction</span>
                        <div class="admin-reaction-list">
                            ${getAuthorReactions().map((r) => `
                                <div class="admin-reaction"
                                     data-id="${comment.id}"
                                     data-reaction="${r}">
                                    <img src="${getReactionImage(r)}" alt="${r}" />
                                    <span class="reaction-name">${getReactionLabel(r)}</span>
                                </div>
                            `).join("")}
                        </div>
                    </div>
                ` : ""}
            </div>

            <div class="comment-body">
                <div class="comment-content">
                    <p class="comment-text">${formatContent(comment.content)}</p>
                </div>

                <img
                    src="${getReactionImage(comment.authorReaction)}"
                    class="admin-reaction ${comment.authorReaction ? "" : "isNull"}"
                />
            </div>

            <div class="comment-footer">
                ${isOwner ? `
                    <div class="comment-action edit" data-id="${comment.id}">Edit</div>
                ` : ""}

                ${(isOwner || admin) ? `
                    <div class="comment-action delete" data-id="${comment.id}">Delete</div>
                ` : ""}
            </div>
        </div>
    `;
}

function formatCommentDate(dateTime) {
    if (!dateTime) {
        return "";
    }

    const date = new Date(dateTime);

    if (Number.isNaN(date.getTime())) {
        return "";
    }

    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
}

function renderList(data) {
    const list = getList();
    list.innerHTML = data.content.map(buildComment).join("");

    currentPage = data.number;
    totalPages = data.totalPages;
}

async function loadComments(page = 0) {
    const res = await fetch(`/comments/topics/${getTopicId()}?page=${page}&size=${COMMENT_PAGE_SIZE}`);
    const data = await res.json();
    renderList(data);
}

async function addComment() {
    const input = document.getElementById("comment-input");
    const content = input.value.trim();
    if (!content) return;

    await fetch("/comments", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            topicId: getTopicId(),
            content: content
        })
    });

    input.value = "";
    await loadComments(0);
}

function startEditComment(id) {
    if (editingCommentId && editingCommentId !== id) {
        loadComments(currentPage);
        return;
    }

    const commentElement = document.getElementById(`comment-${id}`);
    if (!commentElement) return;

    const textElement = commentElement.querySelector(".comment-text");
    const footerElement = commentElement.querySelector(".comment-footer");
    if (!textElement || !footerElement) return;

    const oldContent = textElement.innerText;
    editingCommentId = id;

    textElement.outerHTML = `
        <textarea class="comment-input comment-edit-input" id="edit-input-${id}">${escapeHtml(oldContent)}</textarea>
    `;

    footerElement.innerHTML = `
        <button type="button" class="comment-action edit-submit" data-id="${id}">Submit</button>
        <button type="button" class="comment-action edit-cancel" data-id="${id}">Cancel</button>
        <div class="comment-action delete" data-id="${id}">Delete</div>
    `;
}

async function submitEditComment(id) {
    const input = document.getElementById(`edit-input-${id}`);
    if (!input) return;

    const content = input.value.trim();
    if (!content) return;

    await fetch(`/comments/${id}?content=${encodeURIComponent(content)}`, {
        method: "PUT"
    });

    editingCommentId = null;
    await loadComments(currentPage);
}

function cancelEditComment() {
    editingCommentId = null;
    loadComments(currentPage);
}

async function deleteComment(id) {
    if (!confirm("Delete comment?")) return;

    await fetch(`/comments/${id}`, {
        method: "DELETE"
    });

    editingCommentId = null;
    await loadComments(currentPage);
}

async function reactComment(id, reaction) {
    const url = reaction
        ? `/comments/${id}/author-reaction?authorReaction=${reaction}`
        : `/comments/${id}/author-reaction`;

    await fetch(url, { method: "PATCH" });

    await loadComments(currentPage);
}

function updateReactionPreview(commentElement) {
    const adminReactionComment = commentElement.querySelector(".comment-body .admin-reaction");
    const commentContent = commentElement.querySelector(".comment-body .comment-content");

    if (!adminReactionComment || !commentContent) {
        return;
    }

    if (!adminReactionComment.classList.contains("isNull")) {
        adminReactionComment.style.height = `${commentContent.clientHeight}px`;
    }
}

function initComment() {
    const list = getList();

    list.addEventListener("click", async (e) => {
        const edit = e.target.closest(".edit");
        if (edit) return startEditComment(edit.dataset.id);

        const editSubmit = e.target.closest(".edit-submit");
        if (editSubmit) return submitEditComment(editSubmit.dataset.id);

        const editCancel = e.target.closest(".edit-cancel");
        if (editCancel) return cancelEditComment();

        const del = e.target.closest(".delete");
        if (del) return deleteComment(del.dataset.id);

        const reactionButton = e.target.closest(".admin-react-button");
        if (reactionButton) {
            const comment = reactionButton.closest(".comment");
            const reactionList = comment?.querySelector(".admin-reaction-list");

            if (!reactionList) {
                return;
            }

            reactionList.classList.toggle("show");
            return;
        }

        const reaction = e.target.closest(".admin-reaction-list .admin-reaction");
        if (reaction) {
            const comment = reaction.closest(".comment");
            const reactionList = comment?.querySelector(".admin-reaction-list");
            const adminReactionComment = comment?.querySelector(".comment-body .admin-reaction");
            const commentContent = comment?.querySelector(".comment-body .comment-content");
            const reactionImage = reaction.querySelector("img");

            if (!comment || !reactionList || !adminReactionComment || !commentContent || !reactionImage) {
                return;
            }

            reactionList.classList.remove("show");

            if (adminReactionComment.classList.contains("isNull")) {
                adminReactionComment.classList.remove("isNull");
            }

            adminReactionComment.style.height = `${commentContent.clientHeight}px`;
            adminReactionComment.setAttribute("src", reactionImage.getAttribute("src"));

            await reactComment(reaction.dataset.id, reaction.dataset.reaction);
            return;
        }
    });

    document.getElementById("comment-submit-btn")?.addEventListener("click", addComment);

    document.getElementById("prev-page")?.addEventListener("click", () => {
        if (currentPage > 0) loadComments(currentPage - 1);
    });

    document.getElementById("next-page")?.addEventListener("click", () => {
        if (currentPage < totalPages - 1) loadComments(currentPage + 1);
    });

    loadComments(0);

    document.querySelectorAll(".comment").forEach((comment) => {
        updateReactionPreview(comment);
    });
}

initComment();
