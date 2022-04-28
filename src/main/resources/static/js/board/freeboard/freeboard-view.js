window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('fas fa-comment-alt', '자유게시판');
});

async function deleteFreeboard() {
    await deleteDocs('freeboard');
}

function copyFreeboard() {
    copyDocs('freeboard');
}