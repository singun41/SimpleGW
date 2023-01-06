// 각각의 new, view 페이지에서 공통으로 사용하는 파라미터 추출 함수.
function getParams() {
    let dt = document.getElementsByClassName('input-date-range')[0].value.replaceAll(' ', '').replaceAll('.', '-').split('~');
    let dtFrom = dt[0].substr(0, 10);
    let dtTo;

    if(dt.length === 1)
        dtTo = dtFrom;
    else
        dtTo = dt[1].substr(0, 10);

    let content = document.getElementById('content').value;
    let timeFrom = document.getElementsByClassName('input-time')[0].value;
    let timeTo = document.getElementsByClassName('input-time')[1].value;
    
    let params = {
        dateFrom: dtFrom,
        dateTo: dtTo,
        code: document.getElementById('code').value,
        title: document.getElementById('title').value,
        content: content === '' ? null : content,
        timeFrom: timeFrom === '' ? null : timeFrom.replaceAll(' : ', ':'),   // 공백 제거.
        timeTo: timeTo === '' ? null : timeTo.replaceAll(' : ', ':')
    };

    return params;
}
