$(function() {
    $(".toolbar input").bind("keypress", function(event) {
        if("13" == event.keyCode) {
            $("#job-exec-status-table").bootstrapTable("refresh", {silent: true});
        }
    });
    $("#job-exec-status-table").bootstrapTable({
        url: '/task/get_execute_event_page',
        striped: true,     //使表格带有条纹
        pagination: true, //在表格底部显示分页工具栏
        pageSize: 10,
        pageNumber: 1,
        responseHandler: responseHandler,
        pageList: [10, 20, 50, 100],
        showToggle: false,   //名片格式
        cardView: false,//设置为True时显示名片（card）布局
        showColumns: true, //显示隐藏列
        showRefresh: true,  //显示刷新按钮
        singleSelect: true,//复选框只能选择一条记录
        search: false,//是否显示右上角的搜索框
        clickToSelect: true,//点击行即可选中单选/复选框
        sidePagination: "server",//表格分页的位置
        queryParams: queryParams, //参数
        queryParamsType: "notLimit",
        toolbar: "#jobExecStatusToolbar", //设置工具栏的Id或者class
        silent: true,  //刷新事件必须设置
        formatLoadingMessage: function () {
            return "请稍等，正在加载中...";
        }
    }).on("all.bs.table", function() {
        doLocale();
    });
    bindViewShardExecButton();
    bindViewTraceButton();
});

function queryParams(params) {
    var jobName = $("#job-name").val();
    var startTime = $("#start-time").val();
    var endTime = $("#end-time").val();
    return {page: params.pageNumber,pageSize: params.pageSize,jobName: jobName,startTime: startTime,endTime: endTime};
}

function responseHandler(res) {
    if (res && res.status == 200) {
        return {
            "rows" : res.data.data,
            "total" : res.data.totalCnt
        };
    } else {
        showFailureDialog(res.err);
        return {
            "rows" : [],
            "total" : 0
        };
    }
}


function generateOperationButtons(val, row) {
    var viewShardExecButton = "<button operation='view-shard' class='btn-xs btn-warning' task-id='" + row.id + "' data-lang='operation-view-shard'></button>";
    var viewTraceButton = "<button operation='view-trace' class='btn-xs btn-success' task-id='" + row.id + "' data-lang='operation-view-trace'></button>";
    return viewShardExecButton + "&nbsp;" + viewTraceButton + "&nbsp;";
}

function bindViewShardExecButton() {
    $(document).off("click", "button[operation='view-shard'][data-toggle!='modal']");
    $(document).on("click", "button[operation='view-shard'][data-toggle!='modal']", function(event) {
        var taskId = $(event.currentTarget).attr("task-id");
        $("#index-task-id").text(taskId);
        $("#content").load("/html/history/job_shard_detail.html", null, function(){
            doLocale();
        });
    });
}

function bindViewTraceButton() {
    $(document).off("click", "button[operation='view-trace'][data-toggle!='modal']");
    $(document).on("click", "button[operation='view-trace'][data-toggle!='modal']", function(event) {
        var taskId = $(event.currentTarget).attr("task-id");
        $("#index-task-id").text(taskId);
        $("#content").load("/html/history/job_trace_detail.html", null, function(){
            doLocale();
        });
    });
}
