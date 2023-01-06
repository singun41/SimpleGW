document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');

    flatpickr('.input-date-range', {
        mode: 'range',
        enableTime: false,
        dateFormat: 'Y. m. d.',
        defaultDate: [ dayjs().format('YYYY. MM. DD.'), dayjs().format('YYYY. MM. DD.') ],
        'locale': 'ko',
    });

    flatpickr('.input-time', {
        enableTime: true,
        noCalendar: true,
        dateFormat: 'H : i',
        time_24hr: false,
        'locale': 'ko',
        disableMobile: 'true'
        // mode 기본값인 single이면 모바일 브라우저에서는 기본 date/time/datetime input으로 전환됨. 이 옵션을 전환되지 않게 설정.
    });
});
