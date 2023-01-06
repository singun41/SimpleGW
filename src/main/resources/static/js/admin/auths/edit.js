async function update() {
    if(!confirm('수정하시겠습니까?'))
        return;

    let params = {
        accessible: (document.getElementById('accessible').value === '1' ? true : false),
        rwdRole: document.getElementById('rwdRole').value,
        rwdOther: document.getElementById('rwdOther').value
    };

    let id = document.getElementById('id').innerText;
    let response = await fetchPatchParams(`auths/${id}`, params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok) {
        if(opener)
            opener.getAuths();
        window.close();
    }
}
