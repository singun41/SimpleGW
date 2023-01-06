document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    
    let data = JSON.parse(localStorage.getItem('mainPageSchedule'));
    flatpickrInit(data);
    localStorage.removeItem('mainPageSchedule');
});

function flatpickrInit(data) {
    let receivedData = false;
    if(data)   // iFrame full-calendar 페이지에서 날짜 클릭 이벤트 데이터 --> content 페이지 --> new 페이지
        receivedData = true;

    flatpickr('.input-date-range', {
        mode: 'range',
        enableTime: false,
        dateFormat: 'Y. m. d.',
        defaultDate: [
            receivedData ? dayjs(data.dateFrom).format('YYYY. MM. DD.') : dayjs().format('YYYY. MM. DD.'),
            receivedData ? dayjs(data.dateTo).format('YYYY. MM. DD.') : dayjs().format('YYYY. MM. DD.')
        ],
        'locale': 'ko'
    });

    flatpickr('.input-time', {
        enableTime: true,
        noCalendar: true,
        dateFormat: 'H : i',
        time_24hr: false,
        'locale': 'ko'
    });
}

async function save() {
    if(!confirm('등록하시겠습니까?'))
        return;
    
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

    let response = await fetchPostParams('schedule/personal', params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok) {
        opener.getCalendarData();   // content.js
        window.close();
    }
}
