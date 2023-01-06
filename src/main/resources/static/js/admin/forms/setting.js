let saveComplete = false;
window.addEventListener('beforeunload', event => {
    // 페이지를 나갈 때
    event.preventDefault();
    if(CKEDITOR.instances.ckeditorTextarea.getData() !== '' && !saveComplete) {
        event.returnValue = '';
    }
});

async function getForm() {
    let docs = document.getElementById('forms').value;
    let response = await fetchGet(`docs/form/${docs}`);
    let result = await response.json();
    if(response.ok)
        CKEDITOR.instances.ckeditorTextarea.setData(result.obj);
}

async function saveForm() {
    if(!confirm('저장하시겠습니까?'))
        return;

    let params = {
        content: CKEDITOR.instances.ckeditorTextarea.getData()
    };
    let docs = document.getElementById('forms').value;
    let response = await fetchPatchParams(`docs/form/${docs}`, params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        saveComplete = true;
}
