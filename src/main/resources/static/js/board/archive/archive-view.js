window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('fas fa-file-archive', '자료실');
});

async function deleteArchive() {
    await deleteDocs('archive');
}

function copyArchive() {
    copyDocs('archive');
}