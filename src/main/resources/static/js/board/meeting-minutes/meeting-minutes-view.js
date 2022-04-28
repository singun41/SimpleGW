window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('fas fa-paste', '회의록');
});

async function deleteMeeting() {
    await deleteDocs('meeting');
}

function copyMeeting() {
    copyDocs('meeting');
}