async function update() {
    if(!confirm('수정하시겠습니까?'))
        return;

    let id = document.getElementById('id').value;
    let params = {
        pw: document.getElementById('pw').value
    };
    let response = await fetchPatchParams(`user/${id}/pw`, params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        window.close();
}
