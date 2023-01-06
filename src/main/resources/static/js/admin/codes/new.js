async function save() {
    if(!confirm('등록하시겠습니까?'))
        return;

    let params = {
        seq: document.getElementById('seq').value,
        code: document.getElementById('code').value,
        value: document.getElementById('value').value,
        remarks: document.getElementById('remarks').value,
        enabled: (document.getElementById('enabled').value === '1' ? true : false)
    };

    let response = await fetchPostParams(`basecode/${document.getElementById('type').value}`, params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok) {
        opener.getCodes();
        window.close();
    }
}
