async function del() {
    if(!confirm('삭제하시겠습니까?'))
        return;
    
    let response = await fetchDelete(`schedule/personal/${document.getElementById('scheduleId').innerText}`);
    let result = await response.json();
    alert(result.msg);

    if(response.ok) {
        // if(opener) 를 이용해서 이 함수를 호출했는데 페이지를 벗어나면 동작 안 함.
        // 그래서 창이 닫히지 않는 경우를 방지하기 위해 setTimeout으로 변경.
        setTimeout(() => { window.close(); }, 100);
        opener.sendParams();   // container.js
    }
}
