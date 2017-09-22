$(function() {
    $("#task-id").text($("#index-task-id").text());
    authorityControl();
    renderTraceTable();
});

function renderTraceTable() {
    var taskId = $("#task-id").text();
    $("#trace").bootstrapTable({
        url: "/task/get_trace_event/"+taskId,
        method:'POST',
        cache: false,
        search: true,
        responseHandler: responseHandler,
        showRefresh: true,
        showColumns: true
    }).on("all.bs.table", function() {
        doLocale();
    });
}

function responseHandler(res) {
    if (res && res.status == 200) {
        return res.data;
    }
}

function splitRemarkFormatter(value, row) {
    var maxLength = 50;
    var replacement = "...";
    if(null != value && value.length > maxLength) {
        var valueDetail = value.substring(0 , maxLength - replacement.length) + replacement;
        value = value.replace(/\r\n/g,"<br/>").replace(/\n/g,"<br/>").replace(/\'/g, "\\'");
        var remarkHtml;
        if (3 == row.state) {
            remarkHtml = '<a href="javascript: void(0);" style="color:#FF0000;" onClick="showHistoryMessage(\'' + value + '\')">' + valueDetail + '</a>';
        } else {
            remarkHtml = '<a href="javascript: void(0);" style="color:black;" onClick="showHistoryMessage(\'' + value + '\')">' + valueDetail + '</a>';
        }
        return remarkHtml;
    }
    return value;
}

function executeTypeFormatter(value) {
    if(value == 0){
        return "<span class='label label-success' data-lang='status-ok'></span>";
    }else if(value == 1){
        return "<span class='label label-warning' data-lang='status-failover'></span>";
    }else {
        return "-";
    }
}

function stateFormatter(value) {
    switch(value)
    {
        case 0:
            return "<span class='label label-default' data-lang='status-staging'></span>";
        case 2:
            return "<span class='label label-success' data-lang='status-task-finished'></span>";
        case 1:
            return "<span class='label label-primary' data-lang='status-running'></span>";
        case 3:
            return "<span class='label label-danger' data-lang='status-task-error'></span>";
        default:
            return "-";
    }
}

