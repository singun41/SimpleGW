window.addEventListener('DOMContentLoaded', event => {
    setDatetimePicker('dateStart', '', true);
    setDatetimePicker('dateEnd', '', true);
    dateEndInput.value = moment().add(30, 'minutes').format(formatString);

    getDataFromMain();
    setTypeTitle();
});

let calendarData = '';
function getDataFromMain() {
    try {   // 간헐적으로 opener의 getData()를 가져오지 못하는 경우가 발생하고 있음.
        calendarData = opener.getData();   // main.js의 getData()
        if(calendarData.dateStart !== '' && calendarData.dateEnd !== '') {
            let currentTime = moment().format('HH:mm');
            dateStartInput.value = moment(calendarData.dateStart + ' ' + currentTime).format(formatString);
            dateEndInput.value = moment(calendarData.dateEnd + ' ' + currentTime).add(30, 'minutes').add(-1, 'days').format(formatString);   // fullCalendar에서 endStr이 startStr + 1 되서 들어오므로 -1 days 해준다.
        }
    } catch(err) {
        console.log('opener로부터 getData()함수를 호출하는데 실패하였습니다.' + '\n' + 'Error 원문: ' + err);
    }
}

function setAllDay() {
    let elem = document.getElementById('allDay');

    if(elem.checked) {
        dateStartInput.value = dateStartInput.value.substr(0, 10) + ' 00:00';
        dateEndInput.value = dateEndInput.value.substr(0, 10) + ' 23:59';
    } else {
        dateStartInput.value = moment().format(formatString);
        dateEndInput.value = moment().add(30, 'minutes').format(formatString);
    }
}
