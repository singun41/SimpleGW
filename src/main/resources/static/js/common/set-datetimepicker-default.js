function datetimepickerDefaultSetting() {
    $.fn.datetimepicker.Constructor.Default = $.extend({}, $.fn.datetimepicker.Constructor.Default, {
        icons: {
            time: 'far fa-clock',
            date: 'far fa-calendar',
            up: 'fas fa-arrow-up',
            down: 'fas fa-arrow-down',
            previous: 'fas fa-chevron-left',
            next: 'fas fa-chevron-right',
            today: 'far fa-calendar-check',
            clear: 'fas fa-trash',
            close: 'fas fa-times'
        },
        buttons: {
            showToday: true,
            showClear: false,
            showClose: true
        }
    });
}

function setDatetimePicker(elemId, type, setCurrent) {
    datetimepickerDefaultSetting();
    let formatString = '';
    let localeLang = 'ko';

    if(type === 'date') {
        formatString = 'yyyy-MM-DD';

        $('#' + elemId).datetimepicker({
            dayViewHeaderFormat: 'YYYY년 MM월',
            ignoreReadonly: true,
            format: formatString,
            locale: localeLang
        });
        

    } else if(type === 'time') {
        formatString = 'HH:mm';

        $('#' + elemId).datetimepicker({
            ignoreReadonly: true,
            format: formatString
        });

    } else {
        formatString = 'yyyy-MM-DD HH:mm';

        $('#' + elemId).datetimepicker({
            dayViewHeaderFormat: 'YYYY년 MM월',
            ignoreReadonly: true,
            format: formatString,
            locale: localeLang
        });
    }

    if(setCurrent) {
        document.getElementById(elemId).value = moment().format(formatString);
    }
}