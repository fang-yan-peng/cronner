$(function() {
    tooltipLocale();
    validate();
    bindSubmitJobSettingsForm();
    bindResetForm();
});

function tooltipLocale(){
    for (var i = 0; i < $("[data-toggle='tooltip']").length; i++) {
        var object = $("[data-toggle='tooltip']")[i];
        $(object).attr('title',$.i18n.prop("placeholder-" + object.getAttribute("id"))).tooltip('fixTitle');
    }
}

function bindSubmitJobSettingsForm() {
    $("#update-job-info-btn").on("click", function(){
        var bootstrapValidator = $("#job-config-form").data("bootstrapValidator");
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            var jobName = $("#job-name-cronner").val();
            var type = $("#job-type").val();
            var shardingTotalCount = $("#sharding-total-count").val();
            var jobParameter = $("#job-parameter").val();
            var cron = $("#cron").val();
            var streamingProcess = $("#streaming-process").prop("checked");
            var monitorExecution = $("#monitor-execution").prop("checked");
            var allowSendJobEvent = $("#allow-send-job-event-cronner").prop("checked");
            var failover = $("#failover").prop("checked");
            var misfire = $("#misfire").prop("checked");
            var shardingParameter = $("#sharding-item-parameters").val();
            var description = $("#description").val();
            var reconcileIntervalMinutes = $("#reconcile-interval-minutes").val();
            var postJson = {"jobName": jobName, "type" : type, "shardingTotalCount": shardingTotalCount, "jobParameter": jobParameter, "cron": cron, "streamingProcess": streamingProcess, "monitorExecution": monitorExecution, "failover": failover, "misfire": misfire, "shardingParameter": shardingParameter, "description": description, "reconcileIntervalMinutes":reconcileIntervalMinutes,"allowSendJobEvent": allowSendJobEvent};
            showUpdateConfirmModal();
            $(document).off("click", "#confirm-btn");
            $(document).on("click", "#confirm-btn", function() {
                $("#confirm-dialog").modal("hide");
                submitAjax(postJson);
            });
        }
    });
}

function submitAjax(postJson) {
    $.ajax({
        url: "/job/update",
        type: "PUT",
        data: JSON.stringify(postJson),
        contentType: "application/json;charset=utf-8",
        dataType: "json",
        success: function() {
            $("#data-update-job").modal("hide");
            $("#job-configs").bootstrapTable("refresh");
            showSuccessDialog();
        }
    });
}

function validate() {
    $("#job-config-form").bootstrapValidator({
        message: "This value is not valid",
        feedbackIcons: {
            valid: "glyphicon glyphicon-ok",
            invalid: "glyphicon glyphicon-remove",
            validating: "glyphicon glyphicon-refresh"
        },
        fields: {
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
            cron: {
                validators: {
                    stringLength: {
                        max: 40,
                        message: $.i18n.prop("job-cron-length-limit")
                    },
                    notEmpty: {
                        message: $.i18n.prop("job-cron-not-null")
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
    $("#job-config-form").submit(function(event) {
        event.preventDefault();
    });
}

function bindResetForm() {
    $("#reset").click(function() {
        $("#job-config-form").data("bootstrapValidator").resetForm();
    });
}
