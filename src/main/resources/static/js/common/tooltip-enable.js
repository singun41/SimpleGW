document.addEventListener('DOMContentLoaded', () => {
    // 모든 곳에서 툴팁 활성화 적용.
    let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    let tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

function hideTooltip(e) {
    // html 태그에 onblur="hideTooltip(this)" 를 작성.
    let tooltip = bootstrap.Tooltip.getInstance(e);
    tooltip.hide();
}
