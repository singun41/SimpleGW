const datalist = document.getElementById('datalist');

function destroyDataTable() {
    $('#datatables').DataTable().destroy();
}

function removeDatalist() {
    while(datalist.hasChildNodes())
        datalist.removeChild(datalist.firstChild);
}
