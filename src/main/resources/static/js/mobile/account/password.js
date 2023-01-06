document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnBack').setAttribute('onclick', 'page("account")');
});

async function updatePw() {
    if(!confirm('패스워드를 변경하시겠습니까?'))
        return;

    if(document.getElementById('newPw').value !== document.getElementById('checkPw').value) {
        alert('새 패스워드가 일치하지 않습니다.\n다시 입력하세요.');
        return;
    }

    let params = {
        oldPw: document.getElementById('oldPw').value,
        newPw: document.getElementById('newPw').value
    };
    let response = await fetchPatchParams('password', params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        page('account');
}
