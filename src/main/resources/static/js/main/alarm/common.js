document.addEventListener('DOMContentLoaded', () => {
    flatpickr('.input-date', {
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
