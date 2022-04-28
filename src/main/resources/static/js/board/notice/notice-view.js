window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('fas fa-clipboard-list', '공지사항');
});

async function deleteNotice() {
    await deleteDocs('notice');
}

function copyNotice() {
    copyDocs('notice');
}