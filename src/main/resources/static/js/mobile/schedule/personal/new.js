document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnBack').setAttribute('onclick', 'page("schedule")');
});

async function save() {
    if(!confirm('등록하시겠습니까?'))
        return;

    let params = getParams();   // common.js

    let response = await fetchPostParams('schedule/personal', params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        page('schedule');
}
