$(function() {
    $("#task-id").text($("#index-task-id").text());
    authorityControl();
    renderShardingTable();
});

function renderShardingTable() {
    var taskId = $("#task-id").text();
    $("#sharding-cronner").bootstrapTable({
        url: "/task/get_execute_event/"+taskId,
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
    }else {
        showFailureDialog(res.err)
    }
}


function sourceFormatter(value, row) {
    if(value == 0){
        return "<span class='label label-success' data-lang='status-ok'></span>";
    }else if(value == 1){
        return "<span class='label label-info' data-lang='status-misfire'></span>";
    }else if(value == 2){
        return "<span class='label label-warning' data-lang='status-failover'></span>";
    }
}

function statusFormatter(value, row) {
    if(value == 0){
        return "<span class='label label-info' data-lang='status-ready'></span>";
    } else if(value == 2){
        return "<span class='label label-success' data-lang='execute-result-success'></span>";
    }else if(value == 3){
        return "<span class='label label-danger' data-lang='execute-result-failure'></span>";
    }else if(value == 1){
        return "<span class='label label-warning' data-lang='status-running'></span>";
    }
}


function splitFailReasonFormatter(value, row) {
    var maxLength = 50;
    var replacement = "...";
    if(null != value && value.length > maxLength) {
        var valueDetail = value.substring(0 , maxLength - replacement.length) + replacement;
        value = value.replace(/\r\n/g,"<br/>").replace(/\n/g,"<br/>").replace(/\'/g, "\\'");
        return '<a href="javascript: void(0);" style="color:#FF0000;" onClick="showHistoryMessage(\'' + value + '\')">' + valueDetail + '</a>';
    }
    return value;
}
