$(function() {
    doLocale();
    authorityControl();
    renderJobConfigs();
    validate();
    dealJobConfigModal();
    submitJobConfig();
    bindButtons();
});

function renderJobConfigs() {
    $(".toolbar input").bind("keypress", function(event) {
        if("13" == event.keyCode) {
            $("#job-configs").bootstrapTable("refresh", {silent: true});
        }
    });
    $("#job-configs").bootstrapTable({
        url: '/job/get_job_page',
        dataType: "json",
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
        toolbar: "#jobNameToolbar", //设置工具栏的Id或者class
        silent: true,  //刷新事件必须设置
        formatLoadingMessage: function () {
            return "请稍等，正在加载中...";
        }
    }
    ).on("all.bs.table", function() {
        doLocale();
    });

}

function queryParams(params) {

    var jobName = $("#job-name-submit").val();
    return {jobName: jobName,pageSize: params.pageSize,page: params.pageNumber};
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

function boolFormatter(value, row) {
    if(value == true){
        return "<span class='label label-success' data-lang='status-ok'></span>";
    }else {
        return "<span class='label label-warning' data-lang='status-disabled'></span>";
    }

}

function typeFormatter(value) {
    if(value == 0){
        return "SimpleJob";
    }else if(value == 1){
        return "DataFlow";
    }else if(value == 2){
        return "Script";
    }else {
        return "Unknown";
    }
}

function generateOperationButtons(value, row) {
    var name = row.jobName;
    var modifyButton = "<button operation='modify-job' class='btn-xs btn-primary' job-name='" + name + "' data-lang='operation-update'></button>";
    var detailButton = "<button operation='job-detail' class='btn-xs btn-info' job-name='" + name + "' data-lang='operation-detail'></button>";
    var disableButton = "<button operation='disable-job' class='btn-xs btn-warning' job-name='" + name + "' data-lang='operation-disable'></button>";
    var triggerButton = "<button operation='trigger-job' class='btn-xs btn-info' job-name='" + name + "' data-lang='operation-trigger'></button>";
    var enableButton = "<button operation='enable-job' class='btn-xs btn-success' job-name='" + name + "' data-lang='operation-enable'></button>";
    var shutdownButton = "<button operation='shutdown-job' class='btn-xs btn-danger' job-name='" + name + "' data-lang='operation-shutdown'></button>";
    var operationTd = modifyButton + "&nbsp;" + detailButton + "&nbsp;";
    if (row.status == true) {
        operationTd = operationTd + "&nbsp;" + disableButton + "&nbsp;"+ triggerButton + "&nbsp;";
    } else {
        operationTd = operationTd + "&nbsp;" + enableButton + "&nbsp;";
    }
    operationTd = operationTd + "&nbsp;" + shutdownButton + "&nbsp;";
    return operationTd;
}

function bindButtons() {
    bindModifyButton();
    bindShardingStatusButton();
    bindTriggerButton();
    bindDisableButton();
    bindEnableButton();
    bindShutdownButton();
}

function bindModifyButton() {
    $(document).off("click", "button[operation='modify-job'][data-toggle!='modal']");
    $(document).on("click", "button[operation='modify-job'][data-toggle!='modal']", function(event) {
        var jobName = $(event.currentTarget).attr("job-name");
        $.ajax({
            url: "/job/get_job_by_name",
            type: "POST",
            dataType: "json",
            data: "jobName="+jobName,
            success: function(data) {
                if (data.status == 200) {
                    $(".box-body").remove();
                    $('#update-job-config-body').load('/html/config/job_config_edit.html', null, function() {
                        doLocale();
                        $('#data-update-job').modal({backdrop : 'static', keyboard : true});
                        renderJob(data.data);
                        $("#job-overviews-name").text(jobName);
                    });
                }else {
                    showFailureDialog(data.err);
                }
            }
        });
    });
}

function bindShardingStatusButton() {
    $(document).off("click", "button[operation='job-detail'][data-toggle!='modal']");
    $(document).on("click", "button[operation='job-detail'][data-toggle!='modal']", function(event) {
        var jobName = $(event.currentTarget).attr("job-name");
        $("#index-job-name").text(jobName);
        $("#content").load("/html/config/job_execute_detail.html", null, function(){
            doLocale();
        });
    });
}

function bindTriggerButton() {
    $(document).off("click", "button[operation='trigger-job'][data-toggle!='modal']");
    $(document).on("click", "button[operation='trigger-job'][data-toggle!='modal']", function(event) {
        var jobName = $(event.currentTarget).attr("job-name");
        $.ajax({
            url: "/job/trigger",
            type: "POST",
            dataType: "json",
            data:"jobName="+jobName,
            success: function(data) {
                if(data.status == 200){
                    showSuccessDialog();
                    $("#job-configs").bootstrapTable("refresh");
                }else {
                    showFailureDialog(data.err);
                }
            }
        });
    });
}

function bindDisableButton() {
    $(document).off("click", "button[operation='disable-job'][data-toggle!='modal']");
    $(document).on("click", "button[operation='disable-job'][data-toggle!='modal']", function(event) {
        var jobName = $(event.currentTarget).attr("job-name");
        $.ajax({
            url: "/job/pause",
            type: "POST",
            dataType: "json",
            data:"jobName="+jobName,
            success: function(data) {
                if(data.status == 200){
                    showSuccessDialog();
                    $("#job-configs").bootstrapTable("refresh");
                }else {
                    showFailureDialog(data.err);
                }
            }
        });
    });
}

function bindEnableButton() {
    $(document).off("click", "button[operation='enable-job'][data-toggle!='modal']");
    $(document).on("click", "button[operation='enable-job'][data-toggle!='modal']", function(event) {
        var jobName = $(event.currentTarget).attr("job-name");
        $.ajax({
            url: "/job/resume",
            type: "POST",
            dataType: "json",
            data:"jobName="+jobName,
            success: function(data) {
                if(data.status == 200){
                    showSuccessDialog();
                    $("#job-configs").bootstrapTable("refresh");
                }else {
                    showFailureDialog(data.err);
                }
            }
        });
    });
}

function bindShutdownButton() {
    $(document).off("click", "button[operation='shutdown-job'][data-toggle!='modal']");
    $(document).on("click", "button[operation='shutdown-job'][data-toggle!='modal']", function(event) {
        showShutdownConfirmModal();
        var jobName = $(event.currentTarget).attr("job-name");
        $(document).off("click", "#confirm-btn");
        $(document).on("click", "#confirm-btn", function() {
            $.ajax({
                url: "/job/shutdown",
                type: "POST",
                dataType: "json",
                data: "jobName="+jobName,
                success: function (data) {
                    if(data.status == 200){
                        $("#confirm-dialog").modal("hide");
                        $(".modal-backdrop").remove();
                        $("body").removeClass("modal-open");
                        $("#job-configs").bootstrapTable("refresh");
                    }else {
                        showFailureDialog(data.err);
                    }
                }
            });
        });
    });
}

function dealJobConfigModal() {
    $("#add-job").click(function() {
        $("#add-job-config").modal({backdrop: 'static', keyboard: true});
    });

    $("#close-add-job-form").click(function() {
        $("#add-job-config").on("hide.bs.modal", function () {
            $("#reg-job-form")[0].reset();
        });
        $("#reg-job-form").data("bootstrapValidator").resetForm();
    });
}

function submitJobConfig() {
    $("#add-job-config-btn").on("click", function(event) {
        var bootstrapValidator = $("#reg-job-form").data("bootstrapValidator");
        bootstrapValidator.validate();
        if(bootstrapValidator.isValid()) {
            var jobName = $("#job-name").val();
            var cron = $("#job-cron").val();
            var shardingTotalCount = $("#job-sharding-total-count").val();
            var shardingParameter = $("#job-sharding-item-parameters").val();
            var jobParameter = $("#job-parameter").val();
            var failover = $("#job-failover").prop("checked");
            var misfire = $("#job-misfire").prop("checked");
            var allowSendJobEvent = $("#allow-send-job-event").prop("checked");
            var monitorExecution = $("#job-monitor-execution").prop("checked");
            var streamingProcess = $("#streaming-process").prop("checked");
            var reconcileIntervalMinutes = $("#job-reconcile-interval-minutes").val();
            var type = $("#job-type").val();
            $.ajax({
                url: "/job/add",
                type: "PUT",
                data: JSON.stringify({"jobName": jobName, "cron": cron, "shardingTotalCount": shardingTotalCount, "shardingParameter": shardingParameter, "jobParameter": jobParameter, "failover": failover, "misfire": misfire, "monitorExecution": monitorExecution, "streamingProcess": streamingProcess, "reconcileIntervalMinutes": reconcileIntervalMinutes, "type": type,"allowSendJobEvent":allowSendJobEvent}),
                contentType: "application/json",
                dataType: "json",
                success: function(data) {
                    if (data.status == 200) {
                        $("#add-job-config").on("hide.bs.modal", function() {
                            $("#reg-job-form")[0].reset();
                        });
                        $("#reg-job-form").data("bootstrapValidator").resetForm();
                        $("#add-job-config").modal("hide");
                        $("#job-configs").bootstrapTable("refresh");
                        $(".modal-backdrop").remove();
                        $("body").removeClass("modal-open");
                    }else {
                        showFailureDialog(data.err);
                    }
                }
            });
        }
    });
}

function validate() {
    $("#reg-job-form").bootstrapValidator({
        message: "This value is not valid",
        feedbackIcons: {
            valid: "glyphicon glyphicon-ok",
            invalid: "glyphicon glyphicon-remove",
            validating: "glyphicon glyphicon-refresh"
        },
        fields: {
            jobName: {
                validators: {
                    notEmpty: {
                        message: $.i18n.prop("job-name-not-null")
                    },
                    stringLength: {
                        max: 50,
                        message: $.i18n.prop("job-name-length-limit")
                    },
                    callback: {
                        message: $.i18n.prop("job-name-existed"),
                        callback: function() {
                            var jobName = $("#job-name").val();
                            var result = true;
                            $.ajax({
                                url: "/job/exist",
                                data: "jobName="+jobName,
                                type: "POST",
                                async: false,
                                success: function(data) {
                                    if(data != null && data.status == 200){
                                        result = false;
                                    }
                                }
                            });
                            return result;
                        }
                    }
                }
            },
            cron: {
                validators: {
                    notEmpty: {
                        message: $.i18n.prop("job-cron-not-null")
                    },
                    stringLength: {
                        max: 40,
                        message: $.i18n.prop("job-cron-length-limit")
                    }
                }
            },
            shardingTotalCount: {
                validators: {
                    notEmpty: {
                        message: $.i18n.prop("job-sharding-count-not-null")
                    },
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("job-sharding-count-should-be-integer")
                    }
                }
            },
            shardingParameter: {
                validators: {
                    notEmpty: {
                        message: $.i18n.prop("job-sharding-parameter-not-null")
                    }
                }
            }
        }
    });
    $("#reg-job-form").submit(function(event) {
        event.preventDefault();
    });
}

function renderJob(data) {
    $("#job-name-cronner").attr("value", data.jobName);
    $("#job-type").attr("value", data.type);
    $("#cron").attr("value", data.cron);
    $("#sharding-total-count").attr("value", data.shardingTotalCount);
    $("#job-parameter").attr("value", data.jobParameter);
    $("#allow-send-job-event-cronner").attr("checked",data.allowSendJobEvent);
    $("#job-status").attr("checked",data.status);
    $("#reconcile-interval-minutes").attr("value",data.reconcileIntervalMinutes);
    $("#monitor-execution").attr("checked",data.monitorExecution);
    $("#failover").attr("checked",data.failover);
    $("#misfire").attr("checked",data.misfire);
    $("#streaming-process").attr("checked",data.streamingProcess);
    $("#sharding-item-parameters").text(data.shardingParameter);
    $("#description").text(data.description);
    $("#create-time").attr("value", data.createTime);
    $("#last-success-time").attr("value", data.lastSuccessTime);
    $("#next-execute-time").attr("value", data.nextExecuteTime);
}


