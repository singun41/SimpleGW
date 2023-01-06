document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    initialization();
    getParams();   // getParams() --> container.js.sendParams() --> binding() --> getSchedule()
});

let modalShowBasisCount = 70;

let calendar;
function initialization() {
    let e = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(e, {
        // height: 700,
        aspectRatio: 1.2,
        initialView: 'dayGridMonth',
        expandRows: true,
        headerToolbar: {
            start: 'dayGridMonth,listWeek',
            center: 'title',
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
            let title = document.createElement('span')
            
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
                openView(arg);
        }
    });
    calendar.render();

    document.getElementsByClassName('fc-prev-button')[0].addEventListener('click', () => {
        sendMsgToParent('getParams');
    });
    document.getElementsByClassName('fc-next-button')[0].addEventListener('click', () => {
        sendMsgToParent('getParams');
    });
    document.getElementsByClassName('fc-today-button')[0].addEventListener('click', () => {
        sendMsgToParent('getParams');
    });
}

function openNew(arg) {
    let data = {
        msg: 'openNew',
        dateFrom: arg.startStr,
        dateTo: dayjs(arg.endStr).subtract(1, 'day').format('YYYY-MM-DD')   // 선택한 날짜셀에서 to 값이 +1일 한 값이 나와서 하루를 빼준다.
    };
    sendMsgToParent(data);
}

function openView(arg) {
    let data = {
        msg: 'openView',
        id: arg.event.id
    };
    sendMsgToParent(data);
}

function addEvent(id, dateFrom, dateTo, timeFrom, timeTo, team, jobTitle, name, displayTitle, tippyTitle, color, isData) {
    calendar.addEvent({
        // default property
        id: id,
        start: timeFrom === '' ? dateFrom : `${dateFrom}T${timeFrom}`,   // 시간 값이 있다면 T를 붙여줘야 함.
        end: timeTo === '' ? dayjs(dateTo).add(1, 'day').format('YYYY-MM-DD') : `${dateTo}T${timeTo}`,   // 시간 값이 없을 때 to 날짜까지 제대로 렌더링 하기 위해 하루를 추가해야 함.
        title: displayTitle,   // calendar에 렌더링 될 때 보이는 title
        color: (color !== '' ? `#${color}` : '#ffffff'),
        textColor: '#343a40',
        
        // extendedProps --> calendar option에서 arg.event.extendedProps.명칭으로 사용.
        dateFrom: dayjs(dateFrom).format('YY. MM. DD.'),
        timeFrom: timeFrom,
        dateTo: dayjs(dateTo).format('YY. MM. DD.'),
        timeTo: timeTo,

        team: team,
        jobTitle: jobTitle,
        name: name,
        tippyTitle: tippyTitle,
        isData: isData
    });
}

window.addEventListener('message', receiveMsgFromParent);   // 'document'가 아니라 'window'다.
function receiveMsgFromParent(e) {   // container.js로부터 메시지 수신
    // 전달받은 메시지 = e.data
    binding(e.data);
}

function sendMsgToParent(msg) {   // container.js에게 메시지 전달
    window.parent.postMessage(msg, '*');
}

function getParams() {
    sendMsgToParent('getParams');
}

let type = '';
let option = '';
function binding(data) {
    type = data.type;
    option = data.option;
    getSchedule();
}

async function getSchedule() {
    let params = {
        type: type,
        option: option,
        year: calendar.getDate().getFullYear(),
        month: calendar.getDate().getMonth() + 1
    };

    let response = await fetchGetParams('schedule/list', params);
    let result = await response.json();

    if(response.ok) {
        let arrData = Array.from(result.obj);
        if(arrData.length >= modalShowBasisCount)   // 건수가 많으면 렌더링하는 시간동안 화면이 멈춰있는 것처럼 보인다. 모달창을 띄워준다.
            sendMsgToParent('show');

        // 기존 이벤트 지우기.
        let evtSources = Array.from(calendar.getEvents());
        evtSources.forEach(e => { e.remove(); });

        let env = JSON.parse(localStorage.getItem('env'));
        if(env.calendarHoliday)
            await getHolidays();

        setTimeout(() => {   // 위에서 모달창을 띄울 때까진 기다려야 하므로 잠시 딜레이 해준다.
            arrData.forEach(e => {
                addEvent(
                    e.id, e.dateFrom, e.dateTo,
                    e.timeFrom === null ? '' : e.timeFrom.toString().substr(0, 5),   // null 체크가 여기서만 됨. calendar.addEvent 함수 내에서는 안 된다.
                    e.timeTo === null ? '' : e.timeTo.toString().substr(0, 5),
                    e.team, e.jobTitle, e.name,
                    (type === 'personal' && e.mine) ? `${e.value} - 내 일정` : `${e.value} - ${e.name} ${e.jobTitle}`,
                    e.title,
                    e.colorHex, true
                );
            });

            sendMsgToParent('hide');
        }, 250);   // 100 정도로 낮추면 모달창이 뜨기도 전에 렌더링이 시작되고 완료되면 hide되지 않는 오류가 발생한다.
    }
}

async function getHolidays() {
    let year = calendar.getDate().getFullYear();
    let month = calendar.getDate().getMonth() + 1;

    let response = await fetchGet(`api/calendar/holidays/${year}/${month}`);
    let result = await response.json();
    if(response.ok)
        Array.from(result.obj).forEach(e => {
            addEvent(`${e.index}-holiday`, e.dateFrom, e.dateTo, '', '', '', '', '', e.title, e.title, (e.holiday ? 'f1a9a0' : ''), false);

            if(e.holiday) {   // 공휴일 날짜 텍스트 색상 변경
                Array.from(document.getElementsByClassName('fc-daygrid-day')).forEach(days => {
                    if(days.getAttribute('data-date').toString() === e.dateFrom)
                        days.classList.add('fc-day-sun');
                });
            }
        });
}
