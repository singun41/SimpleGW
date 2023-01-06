async function update() {
    if(!confirm('수정하시겠습니까?'))
        return;
    
    let params = {
        alarmDate: document.getElementById('date').value.replaceAll('. ', '-').replaceAll('.', ''),
        alarmTime: document.getElementById('time').value.replace(' : ', ':'),
        title: document.getElementById('title').value,
        remarks: (document.getElementById('remarks').value === '' ? null : document.getElementById('remarks').value)
    };

    let alarmId = document.getElementById('alarmId').innerText;
    let response = await fetchPatchParams(`alarm/${alarmId}`, params);
    let result = await response.json();
    alert(result.msg);
    if(response.ok) {
        if(opener)
            opener.afterCUD();
        window.close();
    }
}

async function del() {
    if(!confirm('삭제하시겠습니까?'))
        return;
    
    let alarmId = document.getElementById('alarmId').innerText;
    let response = await fetchDelete(`alarm/${alarmId}`);
    let result = await response.json();
    alert(result.msg);
    if(response.ok) {
        if(opener)
            opener.afterCUD();
        window.close();
    }
}
