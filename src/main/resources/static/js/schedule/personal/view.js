document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    flatpickr('.input-date-range', {
        mode: 'range',
        enableTime: false,
        dateFormat: 'Y. m. d.',
        'locale': 'ko'
    });

    flatpickr('.input-time', {
        enableTime: true,
        noCalendar: true,
        dateFormat: 'H : i',
        time_24hr: false,
        'locale': 'ko'
    });
});
