document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    initialization();
    setTimeout(() => {
        getMainCalendarData();
    }, 500);
});

window.addEventListener('message', receiveMsgFromParent);   // 'document'가 아니라 'window'다.
function receiveMsgFromParent(e) {   // content.js로부터 메시지 수신: calendar manual update 용도.
    // 전달받은 메시지 = e.data
    if(e.data === 'updateCalendar')
        updateCalendar();
    else if(e.data === 'getCalendarData')
        getMainCalendarData();
}

function sendMsgToParent(msg) {   // content.js에게 메시지 전달
    window.parent.postMessage(msg, '*');
}

let calendar;
function initialization() {
    let e = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(e, {
        // height: 700,
        aspectRatio: 1.2,
        initialView: 'dayGridMonth',
        expandRows: true,
        headerToolbar: {
            start: 'title',
            center: '',
            end: 'prev,next today'
        },
        slotMinTime: '00:00',
        slotMaxTime: '24:00',

        navLinks: false, // can click day/week names to navigate views
        editable: false,
        selectable: true,
        nowIndicator: true,
        dayMaxEvents: true, // allow "more" link when too many events

        locale: 'ko',
        weekNumbers: true,

        // event에 mouse over시 description을 tooltip으로 보여준다.
        // tippy.js 의 cdn을 추가해야 함.
        eventDidMount: arg => {
            let div = document.createElement('div');
            let datetime = document.createElement('span');
            let title = document.createElement('span');

            if(arg.event.extendedProps.dateFrom === arg.event.extendedProps.dateTo && arg.event.extendedProps.timeFrom === '') {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom}<br>`;

            } else if(arg.event.extendedProps.dateFrom !== arg.event.extendedProps.dateTo && arg.event.extendedProps.timeFrom === '') {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom} ~ ${arg.event.extendedProps.dateTo}<br>`;

            } else if(arg.event.extendedProps.dateFrom === arg.event.extendedProps.dateTo && arg.event.extendedProps.timeFrom !== '') {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom} ${arg.event.extendedProps.timeFrom} ~ ${arg.event.extendedProps.timeTo}<br>`;

            } else {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom} ${arg.event.extendedProps.timeFrom} ~ ${arg.event.extendedProps.dateTo} ${arg.event.extendedProps.timeTo}<br>`;
            }
            
            title.innerText = arg.event.extendedProps.tippyTitle;
            div.append(datetime, title);

            tippy(arg.el, { content: div });
        },

        select: arg => {   // 날짜 클릭 이벤트
            openNew(arg);
            calendar.unselect();
        },

        eventClick: arg => {   // 등록된 이벤트 클릭시
            if(arg.event.extendedProps.isData)   // 공휴일 데이터는 창을 띄우지 않는다.
                openView(arg.event.id);
        }
    });
    calendar.render();

    document.getElementsByClassName('fc-prev-button')[0].addEventListener('click', () => {
        getMainCalendarData();
    });
    document.getElementsByClassName('fc-next-button')[0].addEventListener('click', () => {
        getMainCalendarData();
    });
    document.getElementsByClassName('fc-today-button')[0].addEventListener('click', () => {
        getMainCalendarData();
    });
}

function addEvent(id, dateFrom, dateTo, timeFrom, timeTo, displayTitle, tippyTitle, color, isData) {
    calendar.addEvent({
        id: id,
        title: displayTitle,

        // 메인 페이지 캘린더에 렌더링하는 데이터는 시간이 표시되지 않게 하기 위해 날짜만 넣는다.
        // 날짜만 넣는데 end 값이 기존 날짜값 그대로 들어가면 하루가 누락되어 기간이 제대로 표시되지 않는다. 그래서 1일을 추가해준다.
        start: dateFrom,
        end: dayjs(dateTo).add(1, 'day').format('YYYY-MM-DD'),

        color: (color !== '' ? `#${color}` : '#ffffff'),
        textColor: '#343a40',

        // extendedProps --> calendar option에서 arg.event.extendedProps.명칭으로 사용.
        dateFrom: dayjs(dateFrom).format('YY. MM. DD.'),
        timeFrom: timeFrom,
        dateTo: dayjs(dateTo).format('YY. MM. DD.'),
        timeTo: timeTo,
        tippyTitle: tippyTitle,
        isData: isData
    });
}

async function getHolidays() {
    let year = calendar.getDate().getFullYear();
    let month = calendar.getDate().getMonth() + 1;

    let response = await fetchGet(`api/calendar/holidays/${year}/${month}`);
    let result = await response.json();
    if(response.ok)
        Array.from(result.obj).forEach(e => {
            addEvent(`${e.index}-holiday`, e.dateFrom, e.dateTo, '', '', e.title, e.title, (e.holiday ? 'f1a9a0' : ''), false);
            
            if(e.holiday) {   // 공휴일 날짜 텍스트 색상 변경
                Array.from(document.getElementsByClassName('fc-daygrid-day')).forEach(days => {
                    if(days.getAttribute('data-date').toString() === e.dateFrom)
                        days.classList.add('fc-day-sun');
                });
            }
        });
}

async function getSchedule(option) {
    let params = {
        type: 'personal',
        option: option,
        year: calendar.getDate().getFullYear(),
        month: calendar.getDate().getMonth() + 1
    };

    let response = await fetchGetParams('schedule/list', params);
    let result = await response.json();
    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            addEvent(
                e.id, e.dateFrom, e.dateTo,
                e.timeFrom === null ? '' : e.timeFrom.toString().substr(0, 5),
                e.timeTo === null ? '' : e.timeTo.toString().substr(0, 5),
                e.mine ? e.value : `${e.value}-${e.name} ${e.jobTitle}`,
                e.title,
                e.colorHex, true
            );
        });
    }
}

async function getMainCalendarData() {
    // 기존 이벤트 지우기.
    let evtSources = Array.from(calendar.getEvents());
    evtSources.forEach(e => { e.remove(); });
    
    let env = JSON.parse(localStorage.getItem('env'));
    if(env.mainCalendarTeam)
        await getSchedule('team');
    
    if(env.mainCalendarMine && !env.mainCalendarTeam)   // team 일정에 내 일정도 포함되어 있음.
        await getSchedule('mine');

    if(env.calendarHoliday)
        await getHolidays();
}

async function updateCalendar() {
    let response = await fetchGet('api/calendar/manual');
    if(response.ok)
        getMainCalendarData();
}

function openView(id) {
    window.open(`/main/calendar/${id}`, '', 'width=750, height=550');
}

function openNew(arg) {
    let data = {
        msg: 'openNewSchedulePage',
        dateFrom: arg.startStr,
        dateTo: dayjs(arg.endStr).subtract(1, 'day').format('YYYY-MM-DD')   // 선택한 날짜셀에서 to 값이 +1일 한 값이 나와서 하루를 빼준다.
    };
    sendMsgToParent(data);
}
