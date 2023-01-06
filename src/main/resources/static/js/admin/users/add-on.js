async function update() {
    if(!confirm('수정하시겠습니까?'))
        return;

    let params = {
        dayoffQty: document.getElementById('dayoffQty').value,
        dayoffUse: document.getElementById('dayoffUse').value,
        updateDayoffQty: document.getElementById('chkDayoffQty').checked,
        updateDayoffUse: document.getElementById('chkDayoffUse').checked
    };

    let response = await fetchPatchParams(`user/${document.getElementById('userId').innerText}/dayoff-count`, params);
    let result = await response.json();
    alert(result.msg);
    if(response.ok)
        window.close();
}
