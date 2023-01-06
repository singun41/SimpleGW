document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    initialization();
    getParams();   // getParams() --> list.js.sendParams() --> binding() --> getSchedule()
});

window.addEventListener('message', receiveMsgFromParent);   // 'document'가 아니라 'window'다.
function receiveMsgFromParent(e) {   // container.js로부터 메시지 수신
    // 전달받은 메시지 = e.data
    binding(e.data);
}

function sendMsgToParent(msg) {   // list.js에게 메시지 전달
    window.parent.postMessage(msg, '*');
}

let type = '';
let option = '';
function binding(data) {
    type = data.type;
    option = data.option;
    getSchedule();
}

function getParams() {
    sendMsgToParent('getParams');
}

let calendar;
function initialization() {
    let e = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(e, {
        // height: 700,
        aspectRatio: 0.5,
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
        weekNumbers: false,

        // event에 mouse over시 description을 tooltip으로 보여준다.
        // tippy.js 의 cdn을 추가해야 함.
        eventDidMount: arg => {
            let div = document.createElement('div');
            let datetime = document.createElement('span');
            let title = document.createElement('span');

            if(arg.event.extendedProps.dateFrom === arg.event.extendedProps.dateTo && arg.event.extendedProps.timeFrom === '') {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom}<br>${arg.event.title}<br>`;

            } else if(arg.event.extendedProps.dateFrom !== arg.event.extendedProps.dateTo && arg.event.extendedProps.timeFrom === '') {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom} ~ ${arg.event.extendedProps.dateTo}<br>${arg.event.title}<br>`;

            } else if(arg.event.extendedProps.dateFrom === arg.event.extendedProps.dateTo && arg.event.extendedProps.timeFrom !== '') {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom} ${arg.event.extendedProps.timeFrom} ~ ${arg.event.extendedProps.timeTo}<br>${arg.event.title}<br>`;

            } else {
                datetime.innerHTML = `${arg.event.extendedProps.dateFrom} ${arg.event.extendedProps.timeFrom} ~ ${arg.event.extendedProps.dateTo}<br>${arg.event.extendedProps.timeTo} ${arg.event.title}<br>`;
            }
            
            title.innerText = arg.event.extendedProps.tippyTitle;
            div.append(datetime, title);

            tippy(arg.el, { content: div });
        },

        select: arg => {   // 날짜 클릭 이벤트
            calendar.unselect();
        },

        eventClick: arg => {   // 등록된 이벤트 클릭시
            if(arg.event.extendedProps.isMine) {
                setTimeout(() => {   // 모바일에서 실제 테스트할 경우 tippy 팝업이 뜨지 않는다. 그래서 팝업이 띄워지고 난 후에 알림창이 나오도록 지연을 건다.
                    if(confirm('내 일정을 수정하시겠습니까?')) {
                        openView(arg);
                    }
                }, 200);
            }
        }
    });
    calendar.render();

    document.getElementsByClassName('fc-prev-button')[0].addEventListener('click', () => {
        getSchedule();
    });
    document.getElementsByClassName('fc-next-button')[0].addEventListener('click', () => {
        getSchedule();
    });
    document.getElementsByClassName('fc-today-button')[0].addEventListener('click', () => {
        getSchedule();
    });
}

function addEvent(id, dateFrom, dateTo, timeFrom, timeTo, displayTitle, tippyTitle, isMine, color) {
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
        isMine: isMine
    });
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
        let evtSources = Array.from(calendar.getEvents());
        evtSources.forEach(e => { e.remove(); });

        Array.from(result.obj).forEach(e => {
            addEvent(
                e.id, e.dateFrom, e.dateTo,
                e.timeFrom === null ? '' : e.timeFrom.toString().substr(0, 5),
                e.timeTo === null ? '' : e.timeTo.toString().substr(0, 5),
                (type === 'personal' && e.mine) ? `${e.value} - 내 일정` : `${e.value} - ${e.name} ${e.jobTitle}`,
                e.title, e.mine,e.colorHex
            );
        });
    }
}

function openView(arg) {
    let data = {
        msg: 'openView',
        id: arg.event.id
    };
    sendMsgToParent(data);
}
