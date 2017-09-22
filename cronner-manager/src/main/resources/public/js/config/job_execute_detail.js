$(function() {
    $("#job-name").text($("#index-job-name").text());
    authorityControl();
    renderShardingTable();
    renderBreadCrumbMenu();
    bindButtons();
});

function renderShardingTable() {
    var jobName = $("#job-name").text();
    $("#sharding").bootstrapTable({
        url: "/job/detail/"+jobName,
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


function shardingStatusFormatter(value, row) {
    switch(value) {
        case "DISABLED":
            return "<span class='label label-warning' data-lang='status-normal'></span>";
            break;
        case "RUNNING":
            return "<span class='label label-primary' data-lang='status-running'></span>";
            break;
        case "SHARDING_FLAG":
            return "<span class='label label-info' data-lang='status-sharding-flag'></span>";
            break;
        default:
            return "<span class='label label-default' data-lang='status-staging'></span>";
            break;
    }
}

function failoverFormatter(value, row) {
    return value ? "Y" : "-";
}

function generateOperationButtons(val, row) {
    var disableButton = "<button operation='disable-sharding' class='btn-xs btn-warning' job-name='" + row.jobName + "' instanceId='" + row.serverIp + ":" + row.port + "' data-lang='operation-disable'></button>";
    var enableButton = "<button operation='enable-sharding' class='btn-xs btn-success' job-name='" + row.jobName + "' instanceId='" + row.serverIp + ":" + row.port + "' data-lang='operation-enable'></button>";
    if ("DISABLED" === row.status) {
        return enableButton;
    } else if("SHARDING_FLAG" != row.status){
        return disableButton;
    }
}

function bindButtons() {
    bindDisableButton();
    bindEnableButton();
}

function bindDisableButton() {
    $(document).off("click", "button[operation='disable-sharding']");
    $(document).on("click", "button[operation='disable-sharding']", function(event) {
        var jobName = $("#index-job-name").text();
        var instanceId = $(event.currentTarget).attr("instanceId");
        $.ajax({
            url: "/job/disable",
            data: "jobName="+jobName+"&instanceId="+instanceId,
            type: "POST",
            dataType: "json",
            success: function(data) {
                if(data.status == 200){
                    showSuccessDialog();
                    $("#sharding").bootstrapTable("refresh");
                }else {
                    showFailureDialog(data.err);
                }
            }
        });
    });
}

function bindEnableButton() {
    $(document).off("click", "button[operation='enable-sharding']");
    $(document).on("click", "button[operation='enable-sharding']", function(event) {
        var jobName = $("#index-job-name").text();
        var instanceId = $(event.currentTarget).attr("instanceId");
        $.ajax({
            url: "/job/enable",
            data: "jobName="+jobName+"&instanceId="+instanceId,
            type: "POST",
            dataType: "json",
            success: function (data) {
                if(data.status == 200){
                    showSuccessDialog();
                    $("#sharding").bootstrapTable("refresh");
                }else {
                    showFailureDialog(data.err);
                }
            }
        });
    });
}

function renderBreadCrumbMenu() {
    $("#breadcrumb-job").click(function() {
        $("#content").load("/html/config/job_config_overwrite.html", null, function(){
            doLocale();
        });
    });
}
