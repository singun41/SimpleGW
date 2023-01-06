let inputPw = document.getElementById('pw');
function checkCapsLock(event) {
    let tooltip = bootstrap.Tooltip.getInstance(inputPw);
    if(event.getModifierState('CapsLock')) {
        inputPw.setAttribute('data-bs-original-title', 'CapsLock이 켜져 있습니다.');
        tooltip.show();
    } else {
        tooltip.hide();
    }
}

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
