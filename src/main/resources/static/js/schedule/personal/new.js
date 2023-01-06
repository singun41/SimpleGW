document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');

    let data = JSON.parse(localStorage.getItem('schedule'));
    flatpickrInit(data);
    localStorage.removeItem('schedule');
});

function flatpickrInit(data) {
    let receivedData = false;
    if(data)   // iFrame full-calendar 페이지에서 날짜 클릭 이벤트 데이터 --> container 페이지 --> new 페이지
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

    let params = getParams();   // common.js

    let response = await fetchPostParams('schedule/personal', params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok) {
        opener.sendParams();
        window.close();
    }
}
