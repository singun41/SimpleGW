window.addEventListener('DOMContentLoaded', event => {
    setDatetimePicker('searchDateStart', 'date', true);
    setDatetimePicker('searchDateEnd', 'date', true);
    setDefaultSearchDateInput();
});

const searchDateStart = document.getElementById('searchDateStart');
const searchDateEnd = document.getElementById('searchDateEnd');

function setDefaultSearchDateInput() {
    searchDateStart.value = moment().add(-1, 'months').format('YYYY-MM-DD');
    searchDateEnd.value = moment().format('YYYY-MM-DD');
}

function checkSearchDateStart() {
    if(searchDateEnd.value === '') {
        return;
    }
    if(searchDateStart.value > searchDateEnd.value) {
        searchDateEnd.value = searchDateStart.value;
    }
}

function checkSearchDateEnd() {
    if(searchDateStart.value === '') {
        return;
    }
    if(searchDateStart.value > searchDateEnd.value) {
        searchDateStart.value = searchDateEnd.value;
    }
}

function setDateAgo(type) {
    switch(type) {
        case 'day':
            searchDateStart.value = moment(searchDateStart.value).add(-1, 'days').format('YYYY-MM-DD');
            break;
        case 'month':
            searchDateStart.value = moment(searchDateStart.value).add(-1, 'months').format('YYYY-MM-DD');
            break;
        case 'year':
            searchDateStart.value = moment(searchDateStart.value).add(-1, 'years').format('YYYY-MM-DD');
            break;
    }
}