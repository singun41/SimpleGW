window.addEventListener('DOMContentLoaded', event => {
    // setTimeout(() => {
    //     getContent('meeting/content/');
    // }, 500);
    
    document.getElementById('documentKindTitle').innerText = '회의록';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateBoardDocs("meeting", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateBoardDocs("meeting", 0)');
    }
});
