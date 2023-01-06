async function update() {
    if(!confirm('수정하시겠습니까?'))
        return;

    let params = getParams();   // common.js
    let response = await fetchPatchParams(`schedule/personal/${document.getElementById('scheduleId').innerText}`, params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        window.close();
}
