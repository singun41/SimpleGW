async function del() {
    if(!confirm('삭제하시겠습니까?'))
        return;
    
    let response = await fetchDelete(`schedule/personal/${document.getElementById('scheduleId').innerText}`);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        window.close();
}
