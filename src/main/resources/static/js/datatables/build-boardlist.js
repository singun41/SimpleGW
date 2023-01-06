document.addEventListener('DOMContentLoaded', () => {
    buildDatatable();
});

function buildDatatable() {
    $('#datatables').DataTable({
        language: {
            paginate: {
                previous: '<i class="fa-solid fa-chevron-left"></i>',
                next: '<i class="fa-solid fa-chevron-right"></i>'
            }
        },
        order: [0, 'desc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '9%' },
            { targets: 1, width: '65%' },
            { targets: 2, width: '13%' },
            { targets: 3, width: '13%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 650
    });
}
